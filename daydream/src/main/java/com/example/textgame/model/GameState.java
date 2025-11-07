package com.example.textgame.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "game_state")
public class GameState {

    @Id // 主键
    @Column(name = "user_id")
    private Long id; // 这个 ID 将与 User 的 ID 相同

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // 告诉 JPA 这个 User 映射的 ID 就是本实体的 ID
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "current_node_id", nullable = false)
    private String currentNodeId;

    @Column(name = "last_save_node_id", nullable = false)
    private String lastSaveNodeId;

    @Embedded // 嵌入 PlayerAttributes 的所有字段
    private PlayerAttributes attributes;

    // 构造函数：需要 User 才能创建
    public GameState(User user) {
        this.user = user;
        this.id = user.getId(); // 确保 ID 同步
        this.currentNodeId = "START";
        this.attributes = new PlayerAttributes();
        this.lastSaveNodeId = "START";
    }
}