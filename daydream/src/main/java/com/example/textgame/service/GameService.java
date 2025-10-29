package com.example.textgame.service;

import com.example.textgame.model.GameChoice;
import com.example.textgame.model.GameNode;
import com.example.textgame.model.GameState;
import com.example.textgame.model.PlayerAttributes;
import com.example.textgame.repository.InMemoryGameRepository;
import com.example.textgame.repository.InMemoryGameStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final InMemoryGameRepository gameRepository;
    private final InMemoryGameStateRepository stateRepository;

    /** * 获取或创建玩家的游戏状态
     */
    public GameState getOrCreateGameState(String userId) {
        return stateRepository.findByUserId(userId)
                .orElseGet(() -> {
                    GameState newState = new GameState(userId);
                    return stateRepository.save(newState);
                });
    }

    /**
     * 重新掷骰分配属性
     */
    public GameState rollAttributes(String userId) {
        GameState state = getOrCreateGameState(userId);
        state.getAttributes().roll(); // 调用 PlayerAttributes 中的 roll 方法
        return stateRepository.save(state);
    }

    /**
     * 手动设置玩家属性
     * @param userId 用户 ID
     * @param attributes 包含 insight, resolve, empathy 的 Map
     * @return 更新后的游戏状态
     * @throws IllegalArgumentException 如果点数无效
     */
    public GameState setManualAttributes(String userId, Map<String, Integer> attributes) {
        GameState state = getOrCreateGameState(userId);
        try {
            // 使用新的构造函数或方法来设置和验证属性
            PlayerAttributes newAttrs = new PlayerAttributes(attributes); // 这会调用构造函数进行验证
            state.setAttributes(newAttrs);
            return stateRepository.save(state);
        } catch (IllegalArgumentException e) {
            // 如果验证失败，重新抛出异常
            throw new IllegalArgumentException("设置属性失败: " + e.getMessage());
        }
    }


    /** * 获取当前游戏节点
     */
    public GameNode getCurrentNode(String userId) {
        GameState state = getOrCreateGameState(userId);
        return gameRepository.findNodeById(state.getCurrentNodeId())
                .orElseThrow(() -> new IllegalStateException("无效的游戏节点ID: " + state.getCurrentNodeId()));
    }

    /**
     * (新) 获取指定 ID 的游戏节点 (用于读档)
     */
    private GameNode getNodeById(String nodeId) {
        return gameRepository.findNodeById(nodeId)
                .orElseThrow(() -> new IllegalStateException("无效的游戏节点ID: " + nodeId));
    }

    /** * 玩家做出选择
     */
    public ResponseEntity<GameNode> makeChoice(String userId, String choiceId) {
        GameState state = getOrCreateGameState(userId);
        GameNode currentNode = getCurrentNode(userId);

        // 查找玩家选择的选项
        GameChoice chosenChoice = currentNode.getChoices().stream()
                .filter(c -> c.getChoiceId().equals(choiceId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效的选择ID: " + choiceId)); // 加上 choiceId 方便调试

        // 1. 属性判定
        if (!checkAttributes(state.getAttributes(), chosenChoice.getRequiredAttributes())) {
            // 判定失败，返回一个特殊的"失败"节点
            GameNode failureNode = new GameNode("FAILURE",
                    "你的属性（如洞察、决心）不足，无法做出这个选择。请返回重新选择。",
                    List.of(new GameChoice("FAIL_BACK", "返回", state.getCurrentNodeId(), null, null)) // FAIL_BACK 指回当前节点 ID
            );
            // 返回 400 Bad Request 并携带这个失败节点
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(failureNode);
        }

        // 2. 属性更新
        updateAttributes(state.getAttributes(), chosenChoice.getAttributeChanges());

        // 3. 状态切换（切换到下一个节点）
        state.setCurrentNodeId(chosenChoice.getNextNodeId());
        stateRepository.save(state);

        // 返回新的游戏节点
        GameNode nextNode = gameRepository.findNodeById(state.getCurrentNodeId())
                .orElseThrow(() -> new IllegalStateException("无效的下一个游戏节点ID"));
        return ResponseEntity.ok(nextNode);
    }

    /** * 重置游戏
     */
    public GameNode resetGame(String userId) {
        stateRepository.deleteByUserId(userId);
        GameState newState = getOrCreateGameState(userId); // 创建新状态时会自动 roll
        return gameRepository.findNodeById(newState.getCurrentNodeId()).get();
    }

    /**
     * (新) 保存游戏进度
     * @param userId 用户 ID
     * @return 成功返回 true, 否则 false (虽然内存保存总成功)
     */
    public boolean saveGame(String userId) {
        GameState state = getOrCreateGameState(userId);
        state.setLastSaveNodeId(state.getCurrentNodeId()); // 将当前节点设为存档点
        stateRepository.save(state);
        return true;
    }

    /**
     * (新) 读取游戏进度
     * @param userId 用户 ID
     * @return 返回读档后的游戏节点
     * @throws IllegalStateException 如果没有存档点 (理论上不会发生)
     */
    public GameNode loadGame(String userId) {
        GameState state = getOrCreateGameState(userId);
        String savedNodeId = state.getLastSaveNodeId();
        if (savedNodeId == null || savedNodeId.isEmpty()) {
            // 理论上不会发生，因为构造函数会设置 START
            savedNodeId = "START";
        }
        state.setCurrentNodeId(savedNodeId); // 将当前节点设置为存档点
        stateRepository.save(state);
        return getNodeById(savedNodeId); // 返回存档点的节点信息
    }


    // 检查是否满足属性要求
    private boolean checkAttributes(PlayerAttributes playerAttrs, Map<String, Integer> requiredAttrs) {
        if (requiredAttrs == null || requiredAttrs.isEmpty()) {
            return true;
        }
        for (Map.Entry<String, Integer> entry : requiredAttrs.entrySet()) {
            if (playerAttrs.getAttribute(entry.getKey()) < entry.getValue()) {
                return false; // 有一项属性不满足
            }
        }
        return true;
    }

    // 更新玩家属性
    private void updateAttributes(PlayerAttributes playerAttrs, Map<String, Integer> attributeChanges) {
        if (attributeChanges == null || attributeChanges.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Integer> entry : attributeChanges.entrySet()) {
            playerAttrs.changeAttribute(entry.getKey(), entry.getValue());
        }
    }
}

