package com.example.textgame.controller;

import com.example.textgame.model.User;
import com.example.textgame.service.FileStorageService;
import com.example.textgame.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.security.Principal;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final UserService userService; // 注入 UserService

    /** * 上传用户头像
     */
    @PostMapping("/upload/avatar")
    public ResponseEntity<String> uploadAvatar(Principal principal, @RequestParam("file") MultipartFile file) {
        String username = principal.getName();
        String filename = fileStorageService.storeFile(file, username);
        return ResponseEntity.ok("文件上传成功: " + filename);
    }

    /**
     * (新) 获取当前用户的头像
     */
    @GetMapping("/avatar")
    public ResponseEntity<Resource> getAvatar(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.findByUsername(principal.getName());
        if (user == null || user.getAvatarPath() == null || user.getAvatarPath().isEmpty()) {
            // 如果用户存在但没有头像，抛出404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "未找到头像");
        }

        try {
            Resource resource = fileStorageService.loadFileAsResource(user.getAvatarPath());
            String contentType = "application/octet-stream"; // 默认类型
            // 可以在 FileStorageService 中添加逻辑来猜测 contentType

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "无法加载头像", e);
        }
    }


    /** * 下载心理测试报告
     */
    @GetMapping("/download/report")
    public ResponseEntity<Resource> downloadReport(Principal principal) {
        // 允许匿名下载（如果 principal 为 null）或认证下载
        String userId = (principal != null) ? principal.getName() : "anonymous_report";
        Resource resource = fileStorageService.generatePsychologicalReport(userId);

        String filename = "Psychological_Report_" + userId + ".txt";

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}

