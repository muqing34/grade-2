package com.example.textgame.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList; // (新) 导入 ArrayList

/**
 * (已修改)
 * GameNode 现在代表一个完整的“场景”。
 * 'content' 字段已被 'dialogue' 列表取代。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameNode {
    private String nodeId;

    // (新) 背景图片文件名 (例如: "gaofu.jpg")
    private String background;

    // (新) 场景中的人物立绘列表
    private List<CharacterSprite> sprites = new ArrayList<>();

    // (新) 对话序列
    private List<DialogueLine> dialogue = new ArrayList<>();

    // (保留) 选项列表 (将在对话结束后显示)
    private List<GameChoice> choices = new ArrayList<>();
}