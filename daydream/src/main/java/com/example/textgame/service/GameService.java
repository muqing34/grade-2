package com.example.textgame.service;

import com.example.textgame.model.GameChoice;
import com.example.textgame.model.GameNode;
import com.example.textgame.model.GameState;
import com.example.textgame.model.PlayerAttributes;
import com.example.textgame.repository.InMemoryGameRepository;
import com.example.textgame.repository.InMemoryGameStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final InMemoryGameRepository gameRepository;
    private final InMemoryGameStateRepository stateRepository;

    /**
     * 获取或创建玩家的游戏状态
     */
    public GameState getOrCreateGameState(String userId) {
        return stateRepository.findByUserId(userId)
                .orElseGet(() -> {
                    GameState newState = new GameState(userId);
                    return stateRepository.save(newState);
                });
    }

    /**
     * 获取当前游戏节点
     */
    public GameNode getCurrentNode(String userId) {
        GameState state = getOrCreateGameState(userId);
        return gameRepository.findNodeById(state.getCurrentNodeId())
                .orElseThrow(() -> new IllegalStateException("无效的游戏节点ID: " + state.getCurrentNodeId()));
    }

    /**
     * 玩家做出选择
     */
    public GameNode makeChoice(String userId, String choiceId) {
        GameState state = getOrCreateGameState(userId);
        GameNode currentNode = getCurrentNode(userId);

        // 查找玩家选择的选项
        GameChoice chosenChoice = currentNode.getChoices().stream()
                .filter(c -> c.getChoiceId().equals(choiceId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效的选择ID"));

        // 1. 属性判定
        if (!checkAttributes(state.getAttributes(), chosenChoice.getRequiredAttributes())) {
            // 判定失败，返回一个特殊的“失败”节点或当前节点
            // 这里我们返回一个临时的“阻挡”节点
            GameNode failureNode = new GameNode("FAILURE",
                    "你的属性（如智慧、勇气）不足，无法做出这个选择。请返回重新选择。",
                    List.of(new GameChoice("FAIL_BACK", "返回", state.getCurrentNodeId(), null, null))
            );
            return failureNode;
        }

        // 2. 属性更新
        updateAttributes(state.getAttributes(), chosenChoice.getAttributeChanges());

        // 3. 状态切换（切换到下一个节点）
        state.setCurrentNodeId(chosenChoice.getNextNodeId());
        stateRepository.save(state);

        // 返回新的游戏节点
        return gameRepository.findNodeById(state.getCurrentNodeId())
                .orElseThrow(() -> new IllegalStateException("无效的下一个游戏节点ID"));
    }

    /**
     * 重置游戏
     */
    public GameNode resetGame(String userId) {
        stateRepository.deleteByUserId(userId);
        GameState newState = getOrCreateGameState(userId);
        return gameRepository.findNodeById(newState.getCurrentNodeId()).get();
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
