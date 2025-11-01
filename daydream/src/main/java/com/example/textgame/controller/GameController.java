package com.example.textgame.controller;

import com.example.textgame.model.GameNode;
import com.example.textgame.model.GameState;
import com.example.textgame.dto.GameChoiceRequest;
import com.example.textgame.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus; // 导入 HttpStatus
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map; // 导入 Map

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    /** * 获取当前游戏节点
     */
    @GetMapping("/current-node")
    public ResponseEntity<GameNode> getCurrentNode(Principal principal) {
        String userId = principal.getName(); // Spring Security 会注入用户名
        GameNode currentNode = gameService.getCurrentNode(userId);
        return ResponseEntity.ok(currentNode);
    }

    /** * 获取当前游戏状态（包括属性）
     */
    @GetMapping("/state")
    public ResponseEntity<GameState> getGameState(Principal principal) {
        String userId = principal.getName();
        GameState state = gameService.getOrCreateGameState(userId);
        return ResponseEntity.ok(state);
    }

    /**
     * 重新掷骰分配属性
     */
    @PostMapping("/roll-attributes")
    public ResponseEntity<GameState> rollAttributes(Principal principal) {
        String userId = principal.getName();
        GameState newState = gameService.rollAttributes(userId);
        return ResponseEntity.ok(newState);
    }

    /**
     * 手动设置玩家属性
     * @param principal 当前用户
     * @param attributes 包含 insight, resolve, empathy 的 Map
     * @return 更新后的游戏状态或错误信息
     */
    @PostMapping("/set-attributes")
    public ResponseEntity<?> setManualAttributes(Principal principal, @RequestBody Map<String, Integer> attributes) {
        String userId = principal.getName();
        try {
            GameState updatedState = gameService.setManualAttributes(userId, attributes);
            return ResponseEntity.ok(updatedState);
        } catch (IllegalArgumentException e) {
            // 如果 GameService 抛出验证错误，返回 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // 其他未知错误
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("设置属性时发生错误");
        }
    }


    /** * 做出选择
     */
    @PostMapping("/choice")
    public ResponseEntity<GameNode> makeChoice(Principal principal, @Valid @RequestBody GameChoiceRequest choiceRequest) {
        String userId = principal.getName();
        try {
            return gameService.makeChoice(userId, choiceRequest.getChoiceId());
        } catch (IllegalArgumentException e) {
            // 捕获无效选择 ID 的错误
            System.err.println("Make choice error: " + e.getMessage()); // 打印错误到后端日志
            // 返回 400 Bad Request，可以带上错误信息
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IllegalStateException e) {
            System.err.println("Make choice state error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 游戏数据配置错误
        }
    }

    /** * 重置游戏
     */
    @PostMapping("/reset")
    public ResponseEntity<GameNode> resetGame(Principal principal) {
        String userId = principal.getName();
        GameNode startNode = gameService.resetGame(userId);
        return ResponseEntity.ok(startNode);
    }

    /**
     * (新) 保存游戏
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveGame(Principal principal) {
        String userId = principal.getName();
        boolean success = gameService.saveGame(userId);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "游戏已保存"));
        } else {
            // 理论上内存保存不会失败
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("保存失败");
        }
    }

    /**
     * (新) 读取游戏
     */
    @PostMapping("/load")
    public ResponseEntity<GameNode> loadGame(Principal principal) {
        String userId = principal.getName();
        try {
            GameNode loadedNode = gameService.loadGame(userId);
            return ResponseEntity.ok(loadedNode);
        } catch (IllegalStateException e) {
            System.err.println("Load game state error: " + e.getMessage());
            // 如果存档点无效（理论上不会），返回错误
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}

