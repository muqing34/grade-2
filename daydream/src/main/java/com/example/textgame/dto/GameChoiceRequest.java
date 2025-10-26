package com.example.textgame.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GameChoiceRequest {
    @NotBlank(message = "必须提供选择ID")
    private String choiceId;
}