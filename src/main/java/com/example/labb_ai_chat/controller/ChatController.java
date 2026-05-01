package com.example.labb_ai_chat.controller;

import com.example.labb_ai_chat.dto.ChatRequest;
import com.example.labb_ai_chat.dto.ChatResponse;
import com.example.labb_ai_chat.model.Message;
import com.example.labb_ai_chat.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return chatService.processChat(request);
    }

    @GetMapping("/history/{sessionId}")
    public List<Message> getHistory(@PathVariable String sessionId) {
        return chatService.getHistory(sessionId);
    }
}
