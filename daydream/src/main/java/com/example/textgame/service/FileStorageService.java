package com.example.textgame.service;

import com.example.textgame.model.GameState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final UserService userService;
    private final GameService gameService;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir, UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("无法创建存储目录", ex);
        }
    }

    /**
     * 存储上传的文件（例如头像）
     */
    public String storeFile(MultipartFile file, String username) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String filename = username + "_" + originalFilename;

        try {
            if (filename.contains("..")) {
                throw new RuntimeException("文件名包含无效路径");
            }
            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 更新用户头像路径
            userService.updateUserAvatar(username, targetLocation.toString());

            return filename;
        } catch (IOException ex) {
            throw new RuntimeException("存储文件失败", ex);
        }
    }

    /**
     * 加载文件（例如头像）
     */
    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("未找到文件 " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("未找到文件 " + filename, ex);
        }
    }

    /**
     * 动态生成心理测试报告（内存）
     */
    public Resource generatePsychologicalReport(String userId) {
        GameState state = gameService.getOrCreateGameState(userId);

        // 基于游戏状态生成报告内容
        StringBuilder report = new StringBuilder();
        report.append("--- 心理游戏测试报告 ---\n\n");
        report.append("用户ID: ").append(userId).append("\n\n");
        report.append("最终属性:\n");
        state.getAttributes().getAttributes().forEach((key, value) ->
                report.append("  - ").append(key).append(": ").append(value).append("\n")
        );
        report.append("\n");
        report.append("最终节点: ").append(state.getCurrentNodeId()).append("\n\n");

        // 根据属性和节点给出简单分析
        if (state.getAttributes().getAttribute("wisdom") > 8) {
            report.append("分析: 你倾向于使用智慧解决问题，思维缜密。\n");
        } else if (state.getAttributes().getAttribute("courage") > 8) {
            report.append("分析: 你是一个勇敢的行动者，敢于冒险。\n");
        } else if (state.getAttributes().getAttribute("kindness") > 8) {
            report.append("分析: 你内心善良，乐于助人。\n");
        } else {
            report.append("分析: 你在各个方面都保持着平衡。\n");
        }

        // 将字符串转换为输入流资源
        InputStream is = new ByteArrayInputStream(report.toString().getBytes());
        return new InputStreamResource(is);
    }
}
