package com.example.textgame.controller;

import com.example.textgame.model.KnowledgeArticle;
import com.example.textgame.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    // 注入新的 KnowledgeService
    private final KnowledgeService knowledgeService;

    /**
     * (已更新) 搜索本地数据库的文献
     * @param query 搜索关键词
     * @param perPage 每页数量
     * @param page 页码
     * @return 包含文献列表的 ResponseEntity (Page 格式)
     */
    @GetMapping("/search")
    public ResponseEntity<Page<KnowledgeArticle>> searchArticles(
            @RequestParam(defaultValue = "") String query, // 默认搜索词为空
            @RequestParam(defaultValue = "10") int perPage,
            @RequestParam(defaultValue = "1") int page) {

        // 限制每页数量
        int safePerPage = Math.max(1, Math.min(perPage, 50));
        int safePage = Math.max(1, page);

        Page<KnowledgeArticle> results = knowledgeService.searchArticles(query, safePage, safePerPage);

        if (results.isEmpty()) {
            return ResponseEntity.noContent().build(); // 使用 204 No Content 更合适
        }
        return ResponseEntity.ok(results);
    }
}