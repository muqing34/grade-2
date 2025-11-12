package com.example.textgame.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.util.Random;
import java.util.Map;
import java.io.Serializable; // (新) 导入

@Data
@Embeddable
public class PlayerAttributes implements Serializable { // (新) 实现 Serializable

    // (新) 添加 serialVersionUID
    private static final long serialVersionUID = 1L;

    @Column(name = "attr_insight")
    private Integer insight;

    @Column(name = "attr_resolve")
    private Integer resolve;

    @Column(name = "attr_empathy")
    private Integer empathy;

    private static final int TOTAL_POINTS = 15;
    private static final int MIN_POINTS = 1;
    private static final int MAX_POINTS = 10;

    public PlayerAttributes() {
        roll();
    }

    public PlayerAttributes(Map<String, Integer> initialAttributes) {
        if (!validateManualAttributes(initialAttributes)) {
            throw new IllegalArgumentException("无效的属性点分配。总点数必须为 " + TOTAL_POINTS + "，且每项在 " + MIN_POINTS + " 到 " + MAX_POINTS + " 之间。");
        }
        this.insight = initialAttributes.getOrDefault("insight", MIN_POINTS);
        this.resolve = initialAttributes.getOrDefault("resolve", MIN_POINTS);
        this.empathy = initialAttributes.getOrDefault("empathy", MIN_POINTS);
    }

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

    public void roll() {
        Random rand = new Random();
        int pointsLeft = TOTAL_POINTS;
        int numAttributes = 3;

        int maxForInsight = Math.min(MAX_POINTS, pointsLeft - (numAttributes - 1) * MIN_POINTS);
        this.insight = rand.nextInt(maxForInsight - MIN_POINTS + 1) + MIN_POINTS;
        pointsLeft -= this.insight;
        numAttributes--;

        int maxForResolve = Math.min(MAX_POINTS, pointsLeft - (numAttributes - 1) * MIN_POINTS);
        maxForResolve = Math.min(maxForResolve, pointsLeft - MIN_POINTS);
        this.resolve = rand.nextInt(maxForResolve - MIN_POINTS + 1) + MIN_POINTS;
        pointsLeft -= this.resolve;

        this.empathy = pointsLeft;

        if (this.empathy < MIN_POINTS || this.empathy > MAX_POINTS || this.insight + this.resolve + this.empathy != TOTAL_POINTS) {
            System.err.println("Attribute roll failed validation, rerolling...");
            roll();
        }
    }

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
}