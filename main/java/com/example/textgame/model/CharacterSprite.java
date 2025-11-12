package com.example.textgame.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (新)
 * 表示一个要显示在屏幕上的人物立绘。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterSprite {
    private String image;    // 立绘图片文件名 (例如 "gaoyanzhi.png")
    private String position; // "left", "center", "right"
    // (未来可扩展: "animation: shake", "filter: grayscale(1)")
}