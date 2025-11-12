package com.example.textgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching; // (新) 导入
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableJpaRepositories("com.example.textgame.repository")
@EntityScan("com.example.textgame.model")
@EnableCaching // (新) 启用 Spring 缓存功能
public class TextGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(TextGameApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}