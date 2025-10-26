package com.example.textgame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final RestTemplate restTemplate;

    @Value("${external-api.emotion-url}")
    private String emotionApiUrl;

    @Value("${external-api.knowledge-url}")
    private String knowledgeApiUrl;

    /**
     * 调用情绪分析API（模拟）
     */
    public Map<String, Object> analyzeEmotion(String text) {
        // 在实际应用中，你会使用 restTemplate.postForObject(emotionApiUrl, request, Map.class)
        // 这里我们模拟一个响应
        System.out.println("模拟调用第三方情绪分析API: " + emotionApiUrl);
        System.out.println("分析文本: " + text);

        String emotion = "calm"; // 默认情绪
        if (text.contains("高兴") || text.contains("开心")) {
            emotion = "happy";
        } else if (text.contains("生气") || text.contains("愤怒")) {
            emotion = "angry";
        } else if (text.contains("害怕") || text.contains("恐惧")) {
            emotion = "fear";
        }

        return Map.of(
                "text", text,
                "detectedEmotion", emotion,
                "confidence", Math.random() * 0.5 + 0.5 // 模拟置信度
        );
    }

    /**
     * 获取心理知识文章（模拟）
     */
    public List<Map<String, String>> getKnowledgeArticles() {
        // 实际应用: restTemplate.getForObject(knowledgeApiUrl, List.class)
        // 模拟数据
        System.out.println("模拟调用第三方知识库API: " + knowledgeApiUrl);
        return List.of(
                Map.of("id", "1", "title", "什么是认知行为疗法 (CBT)？", "summary", "CBT是一种常见的心理治疗形式..."),
                Map.of("id", "2", "title", "如何管理日常压力", "summary", "压力是生活的一部分，但可以通过技巧来管理..."),
                Map.of("id", "3", "title", "正念冥想的好处", "summary", "正念有助于提高专注力和情绪调节能力...")
        );
    }
}
