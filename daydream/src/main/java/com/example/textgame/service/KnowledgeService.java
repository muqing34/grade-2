package com.example.textgame.service;

import com.example.textgame.model.KnowledgeArticle;
import com.example.textgame.repository.KnowledgeArticleRepository;
import lombok.RequiredArgsConstructor;
// (新) 移除 @Cacheable
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl; // (新) 导入 PageImpl
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

// (新) 导入 RedisTemplate 和 Duration
import org.springframework.data.redis.core.RedisTemplate;
import java.time.Duration;
import java.util.List; // (新) 导入 List

@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final KnowledgeArticleRepository knowledgeArticleRepository;

    // (新) 注入 RedisTemplate
    // 我们将使用它进行手动缓存，而不是 @Cacheable
    // 注意：这里的 Key 是 String，Value 是 Object (因为我们要存 Page 对象)
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * (新) 完全重写了缓存逻辑
     *
     * 我们不再使用 @Cacheable，因为它无法正确反序列化 Page/Sort 对象。
     * 我们改为“手动缓存”：
     * 1. 自己生成一个缓存键 (Cache Key)。
     * 2. 尝试从 Redis 读取。
     * 3. 如果 Redis 中没有 (Cache Miss)，则查询 MySQL。
     * 4. 将从 MySQL 查到的结果 (Page 对象) 存入 Redis。
     *
     * 这样可以绕过所有关于 Sort 构造函数的序列化错误，同时满足使用 Redis 的要求。
     */
    @Transactional(readOnly = true)
    public Page<KnowledgeArticle> searchArticles(String query, int page, int perPage) {

        // 1. (新) 创建唯一的缓存键
        // 示例: "knowledgeSearch::query=强迫症,page=1,perPage=10"
        String cacheKey = String.format("knowledgeSearch::query=%s,page=%d,perPage=%d", query, page, perPage);

        try {
            // 2. (新) 尝试从 Redis 获取缓存
            Page<KnowledgeArticle> cachedPage = (Page<KnowledgeArticle>) redisTemplate.opsForValue().get(cacheKey);

            if (cachedPage != null) {
                // 缓存命中 (Cache Hit)! 直接返回 Redis 中的数据
                return cachedPage;
            }
        } catch (Exception e) {
            // 如果 Redis 读取失败 (例如数据格式错误)，就删除这个坏的键，然后继续执行
            System.err.println("读取 Redis 缓存失败: " + e.getMessage());
            redisTemplate.delete(cacheKey);
        }

        // 3. (新) 缓存未命中 (Cache Miss) - 执行数据库查询

        // 仍然使用 Sort.by("id") 来确保排序一致性
        Pageable pageable = PageRequest.of(page - 1, perPage, Sort.by("id"));

        Page<KnowledgeArticle> dbPage; // 从数据库获取的 Page 对象

        if (StringUtils.hasText(query)) {
            dbPage = knowledgeArticleRepository.findByTitleContainingOrCategoryContainingOrSummaryContaining(
                    query, query, query, pageable
            );
        } else {
            dbPage = knowledgeArticleRepository.findAll(pageable);
        }

        // 4. (新) 将数据库结果存入 Redis
        // Spring Boot 默认的 RedisTemplate (JdkSerializationRedisSerializer)
        // 可以正确序列化 PageImpl 对象，因为它实现了 Serializable 接口。
        // 我们设置一个10分钟的过期时间。
        try {
            redisTemplate.opsForValue().set(cacheKey, dbPage, Duration.ofMinutes(10));
        } catch (Exception e) {
            // 即使缓存写入失败，也不应该影响用户，所以我们只打印错误
            System.err.println("写入 Redis 缓存失败: " + e.getMessage());
        }

        // 5. 返回从数据库获取的数据
        return dbPage;
    }
}