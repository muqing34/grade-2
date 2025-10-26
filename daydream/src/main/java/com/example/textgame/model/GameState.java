package com.example.textgame.model;

import lombok.Data;

@Data
public class GameState {
    private String userId;
    private String currentNodeId;
    private PlayerAttributes attributes;

    public GameState(String userId) {
        this.userId = userId;
        this.currentNodeId = "START"; // 游戏开始节点
        this.attributes = new PlayerAttributes();
    }
}