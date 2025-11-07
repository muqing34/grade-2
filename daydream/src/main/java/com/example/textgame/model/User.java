package com.example.textgame.model; 

 import lombok.AllArgsConstructor; 
 import lombok.Data; 
 import lombok.NoArgsConstructor; 
 import jakarta.persistence.*; // 引入 JPA 注解 

 @Data 
 @NoArgsConstructor 
 @AllArgsConstructor 
 @Entity // 标记为 JPA 实体类 
 @Table(name = "app_user") // 建议指定表名 
 public class User { 
     
     @Id // 标记为主键 
     @GeneratedValue(strategy = GenerationType.IDENTITY) // 设置主键自增策略 
     private Long id; // 新增 ID 字段 
     
     @Column(unique = true, nullable = false, length = 50) // 约束：唯一且不为空 
     private String username; 
     
     @Column(nullable = false, length = 100) // 约束：不为空，长度适应加密后密码 
     private String password; 

     @Column(name = "avatar_path", length = 255) 
     private String avatarPath; // 存储头像文件路径 
     
     // (需要移除 @AllArgsConstructor 或手动调整，以适应新增的 ID 字段) 
     // 最佳做法是保留 @NoArgsConstructor 和 @Data，然后手动编写需要的构造函数。 
     // 如果使用 Lombok 的 @AllArgsConstructor，它将包含所有字段，包括 id。 
     // 为了简化，你可以暂时保留 @AllArgsConstructor，但业务代码创建新用户时要小心 id 字段。 
 }