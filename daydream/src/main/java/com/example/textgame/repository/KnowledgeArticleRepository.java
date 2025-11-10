package com.example.textgame.repository;

import com.example.textgame.model.KnowledgeArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, Long> {

    /**
     * 根据标题、类别或摘要进行模糊搜索（分页）
     * @param titleQuery 标题关键词
     * @param categoryQuery 类别关键词
     * @param summaryQuery 摘要关键词
     * @param pageable 分页信息
     * @return 分页的文章数据
     */
    Page<KnowledgeArticle> findByTitleContainingOrCategoryContainingOrSummaryContaining(
            String titleQuery,
            String categoryQuery,
            String summaryQuery,
            Pageable pageable
    );

    /**
     * 查找所有文章（分页）
     */
    Page<KnowledgeArticle> findAll(Pageable pageable);
}