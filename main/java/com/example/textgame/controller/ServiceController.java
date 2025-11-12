package com.example.textgame.controller; 

 import org.springframework.security.core.Authentication; 
 import org.springframework.web.bind.annotation.GetMapping; 
 import org.springframework.web.bind.annotation.RestController; 

 /** 
  * 3.7 定义受限访问的业务类 
  */ 
 @RestController 
 public class ServiceController { 
     
     @GetMapping("/hello") 
     public String hello(Authentication auth) { 
         // 检查 auth 是否为 null (如果过滤器配置不当可能发生) 
         if (auth == null) { 
             return "访问失败：无法获取认证信息。"; 
         } 
         System.out.println("Current user: " + auth.getName()); // 观察用户名 
         System.out.println("Authorities: " + auth.getAuthorities()); // 观察用户权限 
         return "已登录用户访问成功"; 
     } 
 }