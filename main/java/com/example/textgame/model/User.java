package com.example.textgame.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.io.Serializable; // (新) 导入

@Data
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class User implements Serializable { // (新) 实现 Serializable

    // (新) 添加 serialVersionUID
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "avatar_path", length = 255)
    private String avatarPath;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}