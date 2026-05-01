package com.example.labb_ai_chat.service;

import com.example.labb_ai_chat.client.LlmClient;
import com.example.labb_ai_chat.dto.ChatRequest;
import com.example.labb_ai_chat.dto.ChatResponse;
import com.example.labb_ai_chat.model.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final PersonalityService personalityService;
    private final ChatMemoryService chatMemoryService;
    private final LlmClient llmClient;

    public ChatService(PersonalityService personalityService,
                       ChatMemoryService chatMemoryService,
                       LlmClient llmClient) {
        this.personalityService = personalityService;
        this.chatMemoryService = chatMemoryService;
        this.llmClient = llmClient;
    }

    public ChatResponse processChat(ChatRequest request) {
        String systemPrompt = personalityService.getSystemPrompt(request.personality());
        List<Message> history = chatMemoryService.getHistory(request.sessionId());

        List<Message> fullContext = new ArrayList<>();
        fullContext.add(new Message("system", systemPrompt));
        fullContext.addAll(history);
        fullContext.add(new Message("user", request.message()));

        String reply = llmClient.getCompletion(fullContext);

        // Update memory
        chatMemoryService.addMessage(request.sessionId(), new Message("user", request.message()));
        chatMemoryService.addMessage(request.sessionId(), new Message("assistant", reply));

        return new ChatResponse(reply);
    }

    public List<Message> getHistory(String sessionId) {
        return chatMemoryService.getHistory(sessionId);
    }
}
