package com.example.textgame.service;

import com.example.textgame.model.*;
import com.example.textgame.repository.GameStateRepository;
import com.example.textgame.repository.InMemoryGameRepository;
import com.example.textgame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
     */
    @Transactional
    public GameState getOrCreateGameState(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("未找到用户: " + username));

        // GameState 的 ID 就是 User 的 ID
        return stateRepository.findById(user.getId())
                .orElseGet(() -> {
                    // 如果不存在，创建新的 GameState 并关联 User
                    GameState newState = new GameState(user);
                    return stateRepository.save(newState);
                });
    }

    /**
     * 重新掷骰分配属性
     */
    @Transactional
    public GameState rollAttributes(String username) {
        GameState state = getOrCreateGameState(username);
        state.getAttributes().roll();
        return stateRepository.save(state);
    }

    /**
     * 手动设置玩家属性
     */
    @Transactional
    public GameState setManualAttributes(String username, Map<String, Integer> attributes) {
        GameState state = getOrCreateGameState(username);
        try {
            PlayerAttributes newAttrs = new PlayerAttributes(attributes); // 验证
            state.setAttributes(newAttrs);
            return stateRepository.save(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("设置属性失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前游戏节点
     */
    @Transactional(readOnly = true)
    public GameNode getCurrentNode(String username) {
        GameState state = getOrCreateGameState(username);
        return gameRepository.findNodeById(state.getCurrentNodeId())
                .orElseThrow(() -> new IllegalStateException("无效的游戏节点ID: " + state.getCurrentNodeId()));
    }

    /**
     * 玩家做出选择
     */
    @Transactional
    public ResponseEntity<GameNode> makeChoice(String username, String choiceId) {
        GameState state = getOrCreateGameState(username);
        GameNode currentNode = getCurrentNode(username);

        GameChoice chosenChoice = currentNode.getChoices().stream()
                .filter(c -> c.getChoiceId().equals(choiceId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效的选择ID: " + choiceId));

        // 1. 属性判定
        if (!checkAttributes(state.getAttributes(), chosenChoice.getRequiredAttributes())) {
            GameNode failureNode = new GameNode("FAILURE",
                    "你的属性（如洞察、决心）不足，无法做出这个选择。请返回重新选择。",
                    List.of(new GameChoice("FAIL_BACK", "返回", state.getCurrentNodeId(), null, null))
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failureNode);
        }

        // 需求 3：记录选择历史
        state.getChoiceHistory().add(chosenChoice.getText());

        // 3. 属性更新
        updateAttributes(state.getAttributes(), chosenChoice.getAttributeChanges());

        // 4. 状态切换
        state.setCurrentNodeId(chosenChoice.getNextNodeId());
        stateRepository.save(state); // 保存状态更改到数据库

        GameNode nextNode = gameRepository.findNodeById(state.getCurrentNodeId())
                .orElseThrow(() -> new IllegalStateException("无效的下一个游戏节点ID"));
        return ResponseEntity.ok(nextNode);
    }

    /**
     * 重置游戏
     */
    @Transactional
    public GameNode resetGame(String username) {
        GameState state = getOrCreateGameState(username);

        state.setCurrentNodeId("START");
        state.setLastSaveNodeId("START");
        state.setAttributes(new PlayerAttributes());

        // 需求 3：清空历史
        state.getChoiceHistory().clear();

        stateRepository.save(state);

        return gameRepository.findNodeById(state.getCurrentNodeId()).get();
    }

    /**
     * 保存游戏进度
     */
    @Transactional
    public boolean saveGame(String username) {
        GameState state = getOrCreateGameState(username);
        state.setLastSaveNodeId(state.getCurrentNodeId());
        stateRepository.save(state);
        return true;
    }

    /**
     * 读取游戏进度
     */
    @Transactional
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

    // (Helper) 获取节点
    private GameNode getNodeById(String nodeId) {
        return gameRepository.findNodeById(nodeId)
                .orElseThrow(() -> new IllegalStateException("无效的游戏节点ID: " + nodeId));
    }


    // (Helper) 检查属性
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

    // (Helper) 更新属性
    private void updateAttributes(PlayerAttributes playerAttrs, Map<String, Integer> attributeChanges) {
        if (attributeChanges == null || attributeChanges.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Integer> entry : attributeChanges.entrySet()) {
            playerAttrs.changeAttribute(entry.getKey(), entry.getValue());
        }
    }
}