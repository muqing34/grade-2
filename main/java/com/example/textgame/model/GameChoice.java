package com.example.textgame.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameChoice {
    private String choiceId;
    private String text; // 选项描述
    private String nextNodeId; // 点击后跳转的下一个节点ID

    // 属性判定：需要满足的属性条件
    private Map<String, Integer> requiredAttributes;

    // 属性变化：选择后引起的属性增减
    private Map<String, Integer> attributeChanges;
}