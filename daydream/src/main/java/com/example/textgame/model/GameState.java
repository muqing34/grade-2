package com.example.textgame.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "game_state")
public class GameState implements Serializable {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // 告诉 Hibernate: "这个'id'字段的值，就是'user'字段的ID"
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "current_node_id", nullable = false)
    private String currentNodeId;

    @Column(name = "last_save_node_id", nullable = false)
    private String lastSaveNodeId;

    @Embedded
    private PlayerAttributes attributes;

    // 需求 3：增加选择历史记录
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_choice_history", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "choice_text")
    private List<String> choiceHistory = new ArrayList<>();

    // 构造函数：需要 User 才能创建
    public GameState(User user) {
        this.user = user;

        // !! 关键修复：确保这一行被移除或注释掉 !!
        // @MapsId 要求 ID 在创建时为 null，它会在 save() 时自动从 this.user 字段复制 ID。
        // this.id = user.getId();

        this.currentNodeId = "START";
        this.attributes = new PlayerAttributes();
        this.lastSaveNodeId = "START";
        this.choiceHistory = new ArrayList<>();
    }
}