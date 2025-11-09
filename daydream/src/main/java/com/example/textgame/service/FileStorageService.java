package com.example.textgame.service;

import com.example.textgame.model.GameState;
import com.example.textgame.model.PlayerAttributes;
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
import java.util.List; // 导入 List

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
        String fileExtension = "";
        try {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } catch (Exception e) {
            fileExtension = ".png"; // 默认
        }
        String filename = username + "_" + System.currentTimeMillis() + fileExtension;


        try {
            if (filename.contains("..")) {
                throw new RuntimeException("文件名包含无效路径");
            }
            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            userService.updateUserAvatar(username, filename);

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
        PlayerAttributes attrs = state.getAttributes();

        // 基于游戏状态生成报告内容
        StringBuilder report = new StringBuilder();
        report.append("--- 心理游戏测试报告 ---\n\n");
        report.append("用户ID: ").append(userId).append("\n\n");
        report.append("最终属性:\n");

        report.append("  - 洞察力 (insight): ").append(attrs.getInsight()).append("\n");
        report.append("  - 决心 (resolve): ").append(attrs.getResolve()).append("\n");
        report.append("  - 同理心 (empathy): ").append(attrs.getEmpathy()).append("\n");

        report.append("\n");
        report.append("最终节点: ").append(state.getCurrentNodeId()).append("\n\n");

        if (attrs.getAttribute("insight") > 8) {
            report.append("分析: 你倾向于使用智慧解决问题，思维缜密。\n");
        } else if (attrs.getAttribute("resolve") > 8) {
            report.append("分析: 你是一个勇敢的行动者，敢于冒险。\n");
        } else if (attrs.getAttribute("empathy") > 8) {
            report.append("分析: 你内心善良，乐于助人。\n");
        } else {
            report.append("分析: 你在各个方面都保持着平衡。\n");
        }

        // 需求 3：添加选择历史
        report.append("\n\n--- 游戏选择历史 ---\n");
        List<String> history = state.getChoiceHistory();
        if (history == null || history.isEmpty()) {
            report.append("  (无选择记录)\n");
        } else {
            int i = 1;
            for (String choice : history) {
                report.append("  ").append(i++).append(". ").append(choice).append("\n");
            }
        }
        // --- 报告结束 ---

        // 将字符串转换为输入流资源
        InputStream is = new ByteArrayInputStream(report.toString().getBytes());
        return new InputStreamResource(is);
    }
}