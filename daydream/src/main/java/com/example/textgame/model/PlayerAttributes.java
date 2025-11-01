package com.example.textgame.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Data
public class PlayerAttributes {
    // 使用Map灵活定义属性
    private Map<String, Integer> attributes = new HashMap<>();
    private static final int TOTAL_POINTS = 15;
    private static final int MIN_POINTS = 1;
    private static final int MAX_POINTS = 10; // 单项最高点数 (可以调整)

    public PlayerAttributes() {
        // 默认随机分配
        roll();
    }

    /**
     * (新) 构造函数，用于手动设置属性
     * @param initialAttributes 包含 insight, resolve, empathy 的 Map
     * @throws IllegalArgumentException 如果点数无效
     */
    public PlayerAttributes(Map<String, Integer> initialAttributes) {
        if (!validateManualAttributes(initialAttributes)) {
            throw new IllegalArgumentException("无效的属性点分配。总点数必须为 " + TOTAL_POINTS + "，且每项在 " + MIN_POINTS + " 到 " + MAX_POINTS + " 之间。");
        }
        // 使用传入的属性覆盖默认值
        this.attributes.put("insight", initialAttributes.getOrDefault("insight", MIN_POINTS));
        this.attributes.put("resolve", initialAttributes.getOrDefault("resolve", MIN_POINTS));
        this.attributes.put("empathy", initialAttributes.getOrDefault("empathy", MIN_POINTS));
    }

    /**
     * (新) 验证手动分配的点数
     */
    private boolean validateManualAttributes(Map<String, Integer> attrs) {
        if (attrs == null || attrs.size() != 3) return false; // 必须是3个属性

        int insight = attrs.getOrDefault("insight", 0);
        int resolve = attrs.getOrDefault("resolve", 0);
        int empathy = attrs.getOrDefault("empathy", 0);

        // 检查总点数和单项范围
        return insight + resolve + empathy == TOTAL_POINTS &&
                insight >= MIN_POINTS && insight <= MAX_POINTS &&
                resolve >= MIN_POINTS && resolve <= MAX_POINTS &&
                empathy >= MIN_POINTS && empathy <= MAX_POINTS;
    }

    /**
     * 随机分配属性，总点数为 TOTAL_POINTS
     */
    public void roll() {
        attributes.clear();
        Random rand = new Random();
        int pointsLeft = TOTAL_POINTS;
        int numAttributes = 3;

        int insight = 0;
        int resolve = 0;
        int empathy = 0;

        // 稍微平衡一点的随机分配
        // 先给 insight 分配
        int maxForInsight = Math.min(MAX_POINTS, pointsLeft - (numAttributes - 1) * MIN_POINTS);
        insight = rand.nextInt(maxForInsight - MIN_POINTS + 1) + MIN_POINTS;
        pointsLeft -= insight;
        numAttributes--;

        // 再给 resolve 分配
        int maxForResolve = Math.min(MAX_POINTS, pointsLeft - (numAttributes - 1) * MIN_POINTS);
        // 确保 resolve 不会取走所有点数，给 empathy 留至少 MIN_POINTS
        maxForResolve = Math.min(maxForResolve, pointsLeft - MIN_POINTS);
        resolve = rand.nextInt(maxForResolve - MIN_POINTS + 1) + MIN_POINTS;
        pointsLeft -= resolve;

        // 剩下的给 empathy
        empathy = pointsLeft;

        // 最终校验 (理论上不需要，但作为保险)
        if (empathy < MIN_POINTS || empathy > MAX_POINTS || insight + resolve + empathy != TOTAL_POINTS) {
            // 如果出现极端情况，重新 roll
            System.err.println("Attribute roll failed validation, rerolling...");
            roll(); // 递归调用直到成功
            return;
        }

        attributes.put("insight", insight); // 洞察力 (智慧)
        attributes.put("resolve", resolve); // 决心 (勇气)
        attributes.put("empathy", empathy); // 同理心 (善良)
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

