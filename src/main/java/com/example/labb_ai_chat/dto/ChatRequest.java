package com.example.labb_ai_chat.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
    @NotBlank(message = "Personality is required") String personality,
    @NotBlank(message = "Message is required") String message,
    @NotBlank(message = "Session ID is required") String sessionId
) {
}
