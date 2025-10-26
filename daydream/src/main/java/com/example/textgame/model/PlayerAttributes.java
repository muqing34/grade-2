package com.example.textgame.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PlayerAttributes {
    // 使用Map灵活定义属性
    private Map<String, Integer> attributes = new HashMap<>();

    public PlayerAttributes() {
        // 初始属性
        attributes.put("wisdom", 5);
        attributes.put("courage", 5);
        attributes.put("kindness", 5);
    }

    public int getAttribute(String key) {
        return attributes.getOrDefault(key, 0);
    }

    public void setAttribute(String key, int value) {
        attributes.put(key, value);
    }

    public void changeAttribute(String key, int delta) {
        attributes.put(key, getAttribute(key) + delta);
    }
}