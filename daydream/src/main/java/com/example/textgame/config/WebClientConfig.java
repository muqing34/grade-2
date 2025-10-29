package com.example.textgame.config;

// import org.springframework.beans.factory.annotation.Qualifier; // 移除未使用的导入
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${deepseek.api.url}")
    private String deepseekApiUrl;

    @Value("${deepseek.api.key}")
    private String deepseekApiKey;

    @Value("${openalex.api.url}")
    private String openalexApiUrl;

    /**
     * WebClient for DeepSeek API. Bean name will be "webClientDeepSeek" by default.
     */
    @Bean
    // @Qualifier("webClientDeepSeek") // 移除这里的 @Qualifier
    public WebClient webClientDeepSeek(WebClient.Builder builder) {
        return builder
                .baseUrl(deepseekApiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + deepseekApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * WebClient for OpenAlex API. Bean name will be "webClientOpenAlex" by default.
     */
    @Bean
    // @Qualifier("webClientOpenAlex") // 移除这里的 @Qualifier
    public WebClient webClientOpenAlex(WebClient.Builder builder) {
        return builder
                .baseUrl(openalexApiUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}

