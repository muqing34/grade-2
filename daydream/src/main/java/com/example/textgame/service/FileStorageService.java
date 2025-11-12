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
            throw new RuntimeException("無法創建存儲目錄", ex);
        }
    }

    /**
     * 存儲上傳的文件（例如頭像）
     */
    public String storeFile(MultipartFile file, String username) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        try {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } catch (Exception e) {
            fileExtension = ".png"; // 默認
        }
        String filename = username + "_" + System.currentTimeMillis() + fileExtension;


        try {
            if (filename.contains("..")) {
                throw new RuntimeException("文件名包含無效路徑");
            }
            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 儲存相對路徑或僅文件名
            userService.updateUserAvatar(username, filename);

            return filename;
        } catch (IOException ex) {
            throw new RuntimeException("儲存文件失敗", ex);
        }
    }

    /**
     * 加載文件（例如頭像）
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
     * 動態生成心理測試報告（內存）
     */
    public Resource generatePsychologicalReport(String userId) {
        GameState state = gameService.getOrCreateGameState(userId);
        PlayerAttributes attrs = state.getAttributes();

        // 基於遊戲狀態生成報告內容
        StringBuilder report = new StringBuilder();
        report.append("--- 心理遊戲測試報告 ---\n\n");
        report.append("用戶ID: ").append(userId).append("\n\n");
        report.append("最終屬性:\n");

        report.append("  - 洞察力 (insight): ").append(attrs.getInsight()).append("\n");
        report.append("  - 決心 (resolve): ").append(attrs.getResolve()).append("\n");
        report.append("  - 同理心 (empathy): ").append(attrs.getEmpathy()).append("\n");

        report.append("\n");
        report.append("最終節點: ").append(state.getCurrentNodeId()).append("\n\n");

        // 根據屬性和節點給出簡單分析
        if (attrs.getAttribute("insight") > 8) {
            report.append("分析: 你傾向於使用智慧解決問題，思維縝密。\n");
        } else if (attrs.getAttribute("resolve") > 8) {
            report.append("分析: 你是一個勇敢的行動者，敢於冒險。\n");
        } else if (attrs.getAttribute("empathy") > 8) {
            report.append("分析: 你內心善良，樂於助人。\n");
        } else {
            report.append("分析: 你在各個方面都保持著平衡。\n");
        }

        // (新) 添加選擇歷史
        report.append("\n--- 你的選擇歷史 ---\n");
        if (state.getChoiceHistory() == null || state.getChoiceHistory().isEmpty()) {
            report.append("你沒有做出任何選擇。\n");
        } else {
            int step = 1;
            for (String choice : state.getChoiceHistory()) {
                report.append(step).append(". ").append(choice).append("\n");
                step++;
            }
        }

        // 將字符串轉換為輸入流資源
        InputStream is = new ByteArrayInputStream(report.toString().getBytes());
        return new InputStreamResource(is);
    }
}