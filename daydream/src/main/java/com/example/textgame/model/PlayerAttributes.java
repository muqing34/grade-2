package com.example.textgame.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
// 移除 Map 和 Random，因为我们将使用固定字段
// import java.util.Map;
// import java.util.Random;
import java.util.Map;
import java.util.Random; // Random 仍然需要用于 roll()

@Data
@Embeddable // 标记为可嵌入类
public class PlayerAttributes {

    // 移除 Map，使用固定字段
    // private Map<String, Integer> attributes = new HashMap<>();

    @Column(name = "attr_insight")
    private Integer insight; // 洞察力 (智慧)

    @Column(name = "attr_resolve")
    private Integer resolve; // 决心 (勇气)

    @Column(name = "attr_empathy")
    private Integer empathy; // 同理心 (善良)

    private static final int TOTAL_POINTS = 15;
    private static final int MIN_POINTS = 1;
    private static final int MAX_POINTS = 10;

    public PlayerAttributes() {
        // 默认随机分配
        roll();
    }

    /**
     * 构造函数，用于手动设置属性
     * @param initialAttributes 包含 insight, resolve, empathy 的 Map
     * @throws IllegalArgumentException 如果点数无效
     */
    public PlayerAttributes(Map<String, Integer> initialAttributes) {
        if (!validateManualAttributes(initialAttributes)) {
            throw new IllegalArgumentException("无效的属性点分配。总点数必须为 " + TOTAL_POINTS + "，且每项在 " + MIN_POINTS + " 到 " + MAX_POINTS + " 之间。");
        }
        this.insight = initialAttributes.getOrDefault("insight", MIN_POINTS);
        this.resolve = initialAttributes.getOrDefault("resolve", MIN_POINTS);
        this.empathy = initialAttributes.getOrDefault("empathy", MIN_POINTS);
    }

    /**
     * 验证手动分配的点数
     */
    private boolean validateManualAttributes(Map<String, Integer> attrs) {
        if (attrs == null || attrs.size() != 3) return false;

        int insightVal = attrs.getOrDefault("insight", 0);
        int resolveVal = attrs.getOrDefault("resolve", 0);
        int empathyVal = attrs.getOrDefault("empathy", 0);

        return insightVal + resolveVal + empathyVal == TOTAL_POINTS &&
                insightVal >= MIN_POINTS && insightVal <= MAX_POINTS &&
                resolveVal >= MIN_POINTS && resolveVal <= MAX_POINTS &&
                empathyVal >= MIN_POINTS && empathyVal <= MAX_POINTS;
    }

    /**
     * 随机分配属性
     */
    public void roll() {
        Random rand = new Random();
        int pointsLeft = TOTAL_POINTS;
        int numAttributes = 3;

        // 分配 insight
        int maxForInsight = Math.min(MAX_POINTS, pointsLeft - (numAttributes - 1) * MIN_POINTS);
        this.insight = rand.nextInt(maxForInsight - MIN_POINTS + 1) + MIN_POINTS;
        pointsLeft -= this.insight;
        numAttributes--;

        // 分配 resolve
        int maxForResolve = Math.min(MAX_POINTS, pointsLeft - (numAttributes - 1) * MIN_POINTS);
        maxForResolve = Math.min(maxForResolve, pointsLeft - MIN_POINTS);
        this.resolve = rand.nextInt(maxForResolve - MIN_POINTS + 1) + MIN_POINTS;
        pointsLeft -= this.resolve;

        // 剩下的给 empathy
        this.empathy = pointsLeft;

        // 校验
        if (this.empathy < MIN_POINTS || this.empathy > MAX_POINTS || this.insight + this.resolve + this.empathy != TOTAL_POINTS) {
            System.err.println("Attribute roll failed validation, rerolling...");
            roll(); // 递归调用直到成功
        }
    }

    // --- Getter/Setter/ChangeAttribute 方法 ---
    // (我们需要手动实现 get/set/change，因为 Map 被移除了)

    public int getAttribute(String key) {
        switch (key) {
            case "insight": return insight != null ? insight : 0;
            case "resolve": return resolve != null ? resolve : 0;
            case "empathy": return empathy != null ? empathy : 0;
            default: return 0;
        }
    }

    public void setAttribute(String key, int value) {
        switch (key) {
            case "insight": this.insight = value; break;
            case "resolve": this.resolve = value; break;
            case "empathy": this.empathy = value; break;
        }
    }

    public void changeAttribute(String key, int delta) {
        setAttribute(key, getAttribute(key) + delta);
    }

    // (Lombok @Data 会自动生成 insight, resolve, empathy 的
    // getter/setter，这对于 JPA 也是必需的)
}