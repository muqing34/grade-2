package com.example.textgame.controller;

import com.example.textgame.dto.OpenAlexDTO; // 引入 OpenAlex DTO
import com.example.textgame.service.OpenAlexService; // 引入 OpenAlex 服务
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono; // 引入 Mono

@RestController
@RequestMapping("/api/knowledge") // 基础路径不变
@RequiredArgsConstructor
public class KnowledgeController {

    private final OpenAlexService openAlexService; // 注入新服务

    /**
     * (新) 搜索 OpenAlex 文献
     * @param query 搜索关键词
     * @param perPage 每页数量 (可选, 默认 10)
     * @param page 页码 (可选, 默认 1)
     * @return 包含文献列表的 ResponseEntity
     */
    @GetMapping("/search")
    public Mono<ResponseEntity<OpenAlexDTO>> searchArticles(
            @RequestParam(defaultValue = "psychology mental health") String query, // 默认搜索心理健康
            @RequestParam(defaultValue = "10") int perPage,
            @RequestParam(defaultValue = "1") int page) {

        // 限制每页数量，防止过大请求
        int safePerPage = Math.max(1, Math.min(perPage, 50));
        int safePage = Math.max(1, page);

        return openAlexService.searchWorks(query, safePerPage, safePage)
                .map(ResponseEntity::ok) // 成功时返回 200 OK 和 DTO
                .defaultIfEmpty(ResponseEntity.notFound().build()); // 如果服务返回空 (或错误被处理后为空)，返回 404
    }

    // --- 不再需要旧的 /articles 和情绪分析 API ---
    // @GetMapping("/articles") ...
    // @PostMapping("/external/analyze-emotion") ...
}

