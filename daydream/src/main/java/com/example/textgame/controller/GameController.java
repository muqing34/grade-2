package com.example.textgame.controller;

import com.example.textgame.model.GameNode;
import com.example.textgame.model.GameState;
import com.example.textgame.dto.GameChoiceRequest;
import com.example.textgame.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    /**
     * 获取当前游戏节点
     */
    @GetMapping("/current-node")
    public ResponseEntity<GameNode> getCurrentNode(Principal principal) {
        String userId = principal.getName(); // Spring Security 会注入用户名
        GameNode currentNode = gameService.getCurrentNode(userId);
        return ResponseEntity.ok(currentNode);
    }

    /**
     * 获取当前游戏状态（包括属性）
     */
    @GetMapping("/state")
    public ResponseEntity<GameState> getGameState(Principal principal) {
        String userId = principal.getName();
        GameState state = gameService.getOrCreateGameState(userId);
        return ResponseEntity.ok(state);
    }

    /**
     * 做出选择
     */
    @PostMapping("/choice")
    public ResponseEntity<GameNode> makeChoice(Principal principal, @Valid @RequestBody GameChoiceRequest choiceRequest) {
        String userId = principal.getName();
        try {
            GameNode nextNode = gameService.makeChoice(userId, choiceRequest.getChoiceId());
            return ResponseEntity.ok(nextNode);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // 或者返回一个包含错误信息的特定 GameNode
        } catch (IllegalStateException e) {
            return ResponseEntity.status(500).body(null); // 游戏数据配置错误
        }
    }

    /**
     * 重置游戏
     */
    @PostMapping("/reset")
    public ResponseEntity<GameNode> resetGame(Principal principal) {
        String userId = principal.getName();
        GameNode startNode = gameService.resetGame(userId);
        return ResponseEntity.ok(startNode);
    }
}
