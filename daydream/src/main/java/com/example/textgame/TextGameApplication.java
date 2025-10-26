package com.example.textgame;
 
 import org.springframework.boot.SpringApplication;
 import org.springframework.boot.autoconfigure.SpringBootApplication;
 import org.springframework.context.annotation.Bean;
 import org.springframework.web.client.RestTemplate;
 
 @SpringBootApplication
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