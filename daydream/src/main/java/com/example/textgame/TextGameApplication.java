package com.example.textgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan; // (新) 导入
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories; // (新) 导入
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableJpaRepositories("com.example.textgame.repository") // (新) 显式指定仓库包
@EntityScan("com.example.textgame.model") // (新) 显式指定实体包
public class TextGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(TextGameApplication.class, args);
    }

    /**
     * 注册 RestTemplate Bean，用于调用第三方API
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}