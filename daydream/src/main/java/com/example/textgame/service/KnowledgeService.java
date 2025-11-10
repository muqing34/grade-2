package com.example.textgame.service;

import com.example.textgame.model.KnowledgeArticle;
import com.example.textgame.repository.KnowledgeArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final KnowledgeArticleRepository knowledgeArticleRepository;

    @Transactional(readOnly = true)
    public Page<KnowledgeArticle> searchArticles(String query, int page, int perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage, Sort.by("id")); // 页码从0开始

        if (StringUtils.hasText(query)) {
            // 如果有搜索词，进行模糊搜索
            return knowledgeArticleRepository.findByTitleContainingOrCategoryContainingOrSummaryContaining(
                    query, query, query, pageable
            );
        } else {
            // 否则返回所有文章
            return knowledgeArticleRepository.findAll(pageable);
        }
    }
}