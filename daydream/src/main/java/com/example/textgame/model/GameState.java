package com.example.textgame.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable; // (新) 导入
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "game_state")
public class GameState implements Serializable { // (新) 实现 Serializable

    // (新) 添加 serialVersionUID
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // 告诉 JPA 这个 User 映射的 ID 就是本实体的 ID
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "current_node_id", nullable = false)
    private String currentNodeId;

    @Column(name = "last_save_node_id", nullable = false)
    private String lastSaveNodeId;

    @Embedded
    private PlayerAttributes attributes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_choice_history", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "choice_text")
    private List<String> choiceHistory = new ArrayList<>();

    // 构造函数：需要 User 才能创建
    public GameState(User user) {
        this.user = user;
        // (BUG 修复) 这一行必须被注释掉或删除！
        // this.id = user.getId();
        this.currentNodeId = "START";
        this.attributes = new PlayerAttributes();
        this.lastSaveNodeId = "START";
        this.choiceHistory = new ArrayList<>();
    }
}