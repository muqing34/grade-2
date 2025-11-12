package com.example.textgame.controller;

import com.example.textgame.dto.DeepSeekDTO;
import com.example.textgame.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono; // 确保导入 Mono

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiChatService aiChatService;

    /**
     * 处理 AI 聊天请求
     * @param messages 聊天历史记录
     * @return AI 的响应消息
     */
    @PostMapping("/chat")
    public Mono<ResponseEntity<String>> chat(@RequestBody List<DeepSeekDTO.Message> messages) {
        // 修正：调用正确的方法名 chatWithAi
        return aiChatService.chatWithAi(messages)
                .map(ResponseEntity::ok) // 将 String 包装在 ResponseEntity 中
                .defaultIfEmpty(ResponseEntity.status(500).body("AI 未能生成响应")); // 处理空响应
    }

}

