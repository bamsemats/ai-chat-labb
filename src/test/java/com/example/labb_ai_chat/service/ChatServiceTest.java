package com.example.labb_ai_chat.service;

import com.example.labb_ai_chat.client.LlmClient;
import com.example.labb_ai_chat.dto.ChatRequest;
import com.example.labb_ai_chat.dto.ChatResponse;
import com.example.labb_ai_chat.model.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private PersonalityService personalityService;

    @Mock
    private ChatMemoryService chatMemoryService;

    @Mock
    private LlmClient llmClient;

    @InjectMocks
    private ChatService chatService;

    @Test
    void shouldOrchestrateChatFlow() {
        ChatRequest request = new ChatRequest("coder", "Hi", "session-1");

        when(personalityService.getSystemPrompt("coder")).thenReturn("You are a coder");
        when(chatMemoryService.getHistory("session-1")).thenReturn(List.of());
        when(llmClient.getCompletion(anyList())).thenReturn("Hello world");

        ChatResponse response = chatService.processChat(request);

        assertEquals("Hello world", response.reply());
        verify(chatMemoryService, times(2)).addMessage(anyString(), any(Message.class));
    }

    @Test
    void shouldReturnHistory() {
        when(chatMemoryService.getHistory("session-1")).thenReturn(List.of(new Message("user", "Hi")));

        List<Message> history = chatService.getHistory("session-1");

        assertEquals(1, history.size());
        assertEquals("Hi", history.get(0).content());
    }
}
