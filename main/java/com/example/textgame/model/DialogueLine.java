package com.example.textgame.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (新)
 * 表示视觉小说中的一句对话。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DialogueLine {
    private String character; // 角色名 (例如 "高杨枝" 或 "旁白")
    private String text;      // 对话内容
}