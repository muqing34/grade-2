package com.example.textgame.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
// (新) 移除 @EnableCaching，因為我們在 KnowledgeService 中手動緩存
public class CacheConfig {

    /**
     * (新) 解決 "RedisTemplate that could not be found" 的錯誤
     *
     * 您的 KnowledgeService 需要一個類型為 RedisTemplate<String, Object> 的 Bean。
     * Spring Boot 默認配置的 Bean 類型不匹配，所以我們在這裡手動定義它。
     *
     * 1. Key 序列化器: 我們使用 StringRedisSerializer，這樣緩存鍵是可讀的字符串。
     * 2. Value 序列化器: 我們使用 JdkSerializationRedisSerializer。
     *
     * 為什麼使用 JdkSerializationRedisSerializer？
     * 因為我們之前所有的錯誤 (Sort, PageImpl 構造函數) 都來自於 Jackson (JSON) 序列化。
     *
     * JDK 序列化器可以正確處理任何實現了 Serializable 接口的對象
     * (您代碼中的 Page, Sort, KnowledgeArticle, GameState 都已實現)，
     * 這可以 100% 繞過之前所有的 JSON 反序列化錯誤。
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 設置 Key 的序列化器為 String
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // 設置 Value 的序列化器為 JDK 序列化
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();
        template.setValueSerializer(jdkSerializer);
        template.setHashValueSerializer(jdkSerializer);

        template.afterPropertiesSet();
        return template;
    }

    // 我們不再需要定義 CacheManager，因為 @Cacheable 導致了太多問題
}