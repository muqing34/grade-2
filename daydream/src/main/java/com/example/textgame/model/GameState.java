package com.example.textgame.model;

import lombok.Data;
import lombok.NoArgsConstructor; // 添加无参构造函数

@Data
@NoArgsConstructor // 添加无参构造函数以适应可能的反序列化场景
public class GameState {
    private String userId;
    private String currentNodeId;
    private PlayerAttributes attributes;
    private String lastSaveNodeId; // (新) 用于记录上次存档的节点ID

    public GameState(String userId) {
        this.userId = userId;
        this.currentNodeId = "START"; // 游戏开始节点
        this.attributes = new PlayerAttributes();
        this.lastSaveNodeId = "START"; // 初始存档点为开始
    }
}

