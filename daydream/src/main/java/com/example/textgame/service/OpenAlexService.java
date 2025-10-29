package com.example.textgame.service;

import com.example.textgame.dto.OpenAlexDTO;
// import lombok.RequiredArgsConstructor; // 移除 Lombok 的构造函数生成
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
// @RequiredArgsConstructor // 移除 Lombok 的构造函数生成，改为手动添加
public class OpenAlexService {

    private final WebClient webClient; // 保留 final

    // (新) 手动添加构造函数，并使用 @Qualifier
    public OpenAlexService(@Qualifier("webClientOpenAlex") WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * 搜索 OpenAlex 的 works (文献)
     * @param searchTerm 搜索关键词 (例如 "psychology cognitive bias")
     * @param perPage 每页数量
     * @param page 页码
     * @return Mono<OpenAlexDTO> 包含搜索结果的响应对象
     */
    public Mono<OpenAlexDTO> searchWorks(String searchTerm, int perPage, int page) {
        // 增加日志记录请求的 URL
        String requestUri = "/works?search=" + searchTerm + "&per-page=" + perPage + "&page=" + page;
        System.out.println("Requesting OpenAlex URL: " + webClient.get().uri(requestUri).toString()); // 打印实际请求的 URL

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/works")
                        .queryParam("search", searchTerm)
                        .queryParam("per-page", perPage)
                        .queryParam("page", page)
                        // 可以添加更多过滤条件, 例如按年份、类型等
                        // .queryParam("filter", "publication_year:2020")
                        .build())
                .retrieve() // 发送请求并获取响应
                .bodyToMono(OpenAlexDTO.class) // 将响应体转换为 DTO
                .doOnError(error -> System.err.println("Error fetching from OpenAlex: " + error.getMessage())); // 简单的错误处理
    }
}

