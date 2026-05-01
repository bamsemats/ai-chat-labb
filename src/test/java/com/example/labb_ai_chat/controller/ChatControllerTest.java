package com.example.labb_ai_chat.controller;

import com.example.labb_ai_chat.dto.ChatRequest;
import com.example.labb_ai_chat.dto.ChatResponse;
import com.example.labb_ai_chat.model.Message;
import com.example.labb_ai_chat.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnReplyOnSuccess() throws Exception {
        ChatRequest request = new ChatRequest("coder", "How to loop?", "session-1");
        ChatResponse response = new ChatResponse("Use for loop.");

        when(chatService.processChat(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Use for loop."));
    }

    @Test
    void shouldReturn400OnValidationError() throws Exception {
        ChatRequest request = new ChatRequest("", "", ""); // Invalid

        mockMvc.perform(post("/api/v1/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturnHistory() throws Exception {
        when(chatService.getHistory("session-1")).thenReturn(List.of(new Message("user", "Hi")));

        mockMvc.perform(get("/api/v1/chat/history/session-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hi"));
    }
}
