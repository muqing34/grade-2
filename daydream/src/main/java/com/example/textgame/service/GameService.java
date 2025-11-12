package com.example.textgame.service;

import com.example.textgame.model.*;
import com.example.textgame.repository.GameStateRepository;
import com.example.textgame.repository.InMemoryGameRepository;
import com.example.textgame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict; // (新) 导入
import org.springframework.cache.annotation.CachePut;    // (新) 导入
import org.springframework.cache.annotation.Cacheable;  // (新) 导入
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GameService {

    private final InMemoryGameRepository gameRepository;
    private final GameStateRepository stateRepository;
    private final UserRepository userRepository;

    /**
     * 获取或创建玩家的游戏状态
     * 1. 尝试从 "gameState" 缓存中按 username (key) 查找
     * 2. 如果未命中，执行方法（从数据库查找或创建）
     * 3. 将返回的 GameState 存入缓存
     */
    @Transactional
    @Cacheable(value = "gameState", key = "#username")
    public GameState getOrCreateGameState(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户: " + username));

        return stateRepository.findById(user.getId())
                .orElseGet(() -> {
                    GameState newState = new GameState(user);
                    return stateRepository.save(newState);
                });
    }

    /**
     * 重新掷骰
     * 1. 总是执行方法
     * 2. 将返回的 GameState 更新（Put）到 "gameState" 缓存中
     */
    @Transactional
    @CachePut(value = "gameState", key = "#username")
    public GameState rollAttributes(String username) {
        GameState state = getOrCreateGameState(username);
        state.getAttributes().roll();
        return stateRepository.save(state);
    }

    /**
     * 手动设置玩家属性
     * 1. 总是执行方法
     * 2. 将返回的 GameState 更新（Put）到 "gameState" 缓存中
     */
    @Transactional
    @CachePut(value = "gameState", key = "#username")
    public GameState setManualAttributes(String username, Map<String, Integer> attributes) {
        GameState state = getOrCreateGameState(username);
        try {
            PlayerAttributes newAttrs = new PlayerAttributes(attributes);
            state.setAttributes(newAttrs);
            return stateRepository.save(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("设置属性失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前游戏节点 (此方法不直接操作缓存，但它调用的 getOrCreateGameState 会)
     */
    @Transactional(readOnly = true)
    public GameNode getCurrentNode(String username) {
        GameState state = getOrCreateGameState(username); // 将从缓存读取
        return gameRepository.findNodeById(state.getCurrentNodeId())
                .orElseThrow(() -> new IllegalStateException("无效的游戏节点ID: " + state.getCurrentNodeId()));
    }

    /**
     * 玩家做出选择
     * 1. 总是执行方法
     * 2. 方法执行完毕后，从 "gameState" 缓存中移除（Evict）该用户的状态
     * 3. 下次调用 getOrCreateGameState 时将从数据库重新加载新状态
     */
    @Transactional
    @CacheEvict(value = "gameState", key = "#username")
    public ResponseEntity<GameNode> makeChoice(String username, String choiceId) {
        GameState state = getOrCreateGameState(username); // 读取（可能来自缓存）
        GameNode currentNode = getCurrentNode(username);

        GameChoice chosenChoice = currentNode.getChoices().stream()
                .filter(c -> c.getChoiceId().equals(choiceId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效的选择ID: " + choiceId));

        if (!checkAttributes(state.getAttributes(), chosenChoice.getRequiredAttributes())) {
            GameNode failureNode = new GameNode("FAILURE",
                    "你的属性（如洞察、决心）不足，无法做出这个选择。请返回重新选择。",
                    List.of(new GameChoice("FAIL_BACK", "返回", state.getCurrentNodeId(), null, null))
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failureNode);
        }

        state.getChoiceHistory().add(chosenChoice.getText());
        updateAttributes(state.getAttributes(), chosenChoice.getAttributeChanges());
        state.setCurrentNodeId(chosenChoice.getNextNodeId());
        stateRepository.save(state); // 保存到数据库

        GameNode nextNode = gameRepository.findNodeById(state.getCurrentNodeId())
                .orElseThrow(() -> new IllegalStateException("无效的下一个游戏节点ID"));
        return ResponseEntity.ok(nextNode);
    }

    /**
     * 重置游戏
     * 1. 总是执行方法
     * 2. 方法执行完毕后，移除（Evict）缓存
     */
    @Transactional
    @CacheEvict(value = "gameState", key = "#username")
    public GameNode resetGame(String username) {
        GameState state = getOrCreateGameState(username);

        state.setCurrentNodeId("START");
        state.setLastSaveNodeId("START");
        state.setAttributes(new PlayerAttributes());
        state.getChoiceHistory().clear();

        stateRepository.save(state);

        return gameRepository.findNodeById(state.getCurrentNodeId()).get();
    }

    /**
     * 保存游戏进度
     * 1. 总是执行方法
     * 2. 方法执行完毕后，移除（Evict）缓存
     */
    @Transactional
    @CacheEvict(value = "gameState", key = "#username")
    public boolean saveGame(String username) {
        GameState state = getOrCreateGameState(username);
        state.setLastSaveNodeId(state.getCurrentNodeId());
        stateRepository.save(state);
        return true;
    }

    /**
     * 读取游戏进度
     * 1. 总是执行方法
     * 2. 方法执行完毕后，移除（Evict）缓存
     */
    @Transactional
    @CacheEvict(value = "gameState", key = "#username")
    public GameNode loadGame(String username) {
        GameState state = getOrCreateGameState(username);
        String savedNodeId = state.getLastSaveNodeId();
        if (savedNodeId == null || savedNodeId.isEmpty()) {
            savedNodeId = "START";
        }
        state.setCurrentNodeId(savedNodeId);
        stateRepository.save(state);
        return getNodeById(savedNodeId);
    }

    private GameNode getNodeById(String nodeId) {
        return gameRepository.findNodeById(nodeId)
                .orElseThrow(() -> new IllegalStateException("无效的游戏节点ID: " + nodeId));
    }

    private boolean checkAttributes(PlayerAttributes playerAttrs, Map<String, Integer> requiredAttrs) {
        if (requiredAttrs == null || requiredAttrs.isEmpty()) {
            return true;
        }
        for (Map.Entry<String, Integer> entry : requiredAttrs.entrySet()) {
            if (playerAttrs.getAttribute(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    private void updateAttributes(PlayerAttributes playerAttrs, Map<String, Integer> attributeChanges) {
        if (attributeChanges == null || attributeChanges.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Integer> entry : attributeChanges.entrySet()) {
            playerAttrs.changeAttribute(entry.getKey(), entry.getValue());
        }
    }
}