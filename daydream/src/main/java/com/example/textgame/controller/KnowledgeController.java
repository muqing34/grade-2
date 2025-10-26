package com.example.textgame.controller;

import com.example.textgame.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class KnowledgeController {

    private final ExternalApiService externalApiService;

    /**
     * 获取心理知识文章（公开）
     */
    @GetMapping("/knowledge/articles")
    public ResponseEntity<List<Map<String, String>>> getArticles() {
        return ResponseEntity.ok(externalApiService.getKnowledgeArticles());
    }

    /**
     * 调用情绪分析API（需要认证）
     */
    @PostMapping("/external/analyze-emotion")
    public ResponseEntity<Map<String, Object>> analyzeEmotion(@RequestBody String text) {
        // 假设 text 是一个简单的字符串
        return ResponseEntity.ok(externalApiService.analyzeEmotion(text));
    }
}
