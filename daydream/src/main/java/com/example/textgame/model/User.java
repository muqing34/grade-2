package com.example.textgame.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*; // 引入 JPA 注解

@Data
@NoArgsConstructor
// @AllArgsConstructor (BUG 修复) 移除 AllArgsConstructor
@Entity // 标记为 JPA 实体类
@Table(name = "app_user") // 建议指定表名
public class User {

    @Id // 标记为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 设置主键自增策略
    private Long id; // 新增 ID 字段

    @Column(unique = true, nullable = false, length = 50) // 约束：唯一且不为空
    private String username;

    @Column(nullable = false, length = 100) // 约束：不为空，长度适应加密后密码
    private String password;

    @Column(name = "avatar_path", length = 255)
    private String avatarPath; // 存储头像文件路径

    // (BUG 修复) 添加一个用于注册新用户的构造函数
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}