package com.example.textgame.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "knowledge_article")
public class KnowledgeArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 标题

    private String category; // 类别 (如: 焦虑障碍)

    @Column(columnDefinition = "TEXT")
    private String summary; // 摘要

    @Column(columnDefinition = "TEXT")
    private String content; // 完整内容 (Markdown 格式)
}