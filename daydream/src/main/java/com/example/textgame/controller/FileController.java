package com.example.textgame.controller;

import com.example.textgame.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * 上传用户头像
     */
    @PostMapping("/upload/avatar")
    public ResponseEntity<String> uploadAvatar(Principal principal, @RequestParam("file") MultipartFile file) {
        String username = principal.getName();
        String filename = fileStorageService.storeFile(file, username);
        return ResponseEntity.ok("文件上传成功: " + filename);
    }

    /**
     * 下载心理测试报告
     */
    @GetMapping("/download/report")
    public ResponseEntity<Resource> downloadReport(Principal principal) {
        String userId = principal.getName();
        Resource resource = fileStorageService.generatePsychologicalReport(userId);

        String filename = "Psychological_Report_" + userId + ".txt";

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
