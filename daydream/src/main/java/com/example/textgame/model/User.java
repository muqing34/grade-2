package com.example.textgame.model;
 
 import lombok.AllArgsConstructor;
 import lombok.Data;
 import lombok.NoArgsConstructor;
 
 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 public class User {
     private String username;
     private String password; // 存储加密后的密码
     private String avatarPath; // 存储头像文件路径
 }