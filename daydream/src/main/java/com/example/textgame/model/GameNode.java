package com.example.textgame.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameNode {
    private String nodeId;
    private String content; // 节点内容/故事描述
    private List<GameChoice> choices; // 可用选项
}