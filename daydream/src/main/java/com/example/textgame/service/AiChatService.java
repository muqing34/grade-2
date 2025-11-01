package com.example.textgame.service;

import com.example.textgame.dto.DeepSeekDTO;
// import lombok.RequiredArgsConstructor; // 移除
import org.springframework.beans.factory.annotation.Qualifier; // 确保导入 Qualifier
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
// @RequiredArgsConstructor // 移除
public class AiChatService {

    private final WebClient webClient; // final 确保注入
    private final String deepseekApiKey; // 直接存储 Key

    // (新) 手动添加构造函数，并使用正确的 Qualifier
    public AiChatService(@Qualifier("webClientDeepSeek") WebClient webClient, // 注入正确的 Bean 名称
                         @Value("${deepseek.api.key}") String deepseekApiKey) {
        this.webClient = webClient;
        this.deepseekApiKey = deepseekApiKey; // 存储 Key
    }

    /**
     * 调用 DeepSeek API 进行聊天
     * @param messages 聊天历史记录
     * @return AI 的响应消息
     */
    public Mono<String> chatWithAi(List<DeepSeekDTO.Message> messages) {
        DeepSeekDTO.ChatRequest requestPayload = new DeepSeekDTO.ChatRequest();
        requestPayload.setModel("deepseek-chat"); // 或者您希望使用的模型
        requestPayload.setMessages(messages);
        // 可以设置其他参数，如 temperature, max_tokens 等
        // requestPayload.setTemperature(0.7);

        return webClient.post()
                .uri("/chat/completions") // DeepSeek 的聊天端点
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + deepseekApiKey) // 每次请求都添加Key
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestPayload)
                .retrieve()
                .bodyToMono(DeepSeekDTO.ChatResponse.class)
                .map(response -> {
                    if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                        // 返回第一个选择的消息内容
                        return response.getChoices().get(0).getMessage().getContent();
                    }
                    return "抱歉，AI没有返回有效的响应。";
                })
                .doOnError(error -> System.err.println("Error calling DeepSeek API: " + error.getMessage()))
                .onErrorReturn("调用AI服务时出错，请稍后再试。"); // 错误处理
    }

    /**
     * (保留) 根据天气和用户偏好推荐游戏 (这里简化为仅基于天气文本)
     * @param weatherText 天气描述
     * @return 游戏推荐文本
     */
    public Mono<String> recommendGameBasedOnWeather(String weatherText) {
        // 构建发送给 DeepSeek 的消息
        List<DeepSeekDTO.Message> messages = List.of(
                new DeepSeekDTO.Message("system", "你是一个游戏推荐助手。根据用户提供的天气情况，推荐一个适合的简单心理小游戏或活动。"),
                new DeepSeekDTO.Message("user", "今天的天气是：" + weatherText + "。请推荐一个适合的心理小游戏或活动。")
        );
        return chatWithAi(messages); // 复用聊天逻辑
    }
}

