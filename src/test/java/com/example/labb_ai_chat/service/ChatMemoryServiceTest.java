package com.example.labb_ai_chat.service;

import com.example.labb_ai_chat.model.Message;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatMemoryServiceTest {

    private final ChatMemoryService chatMemoryService = new ChatMemoryService();

    @Test
    void shouldStoreAndRetrieveMessages() {
        String sessionId = "session-1";
        Message msg = new Message("user", "Hello");
        chatMemoryService.addMessage(sessionId, msg);

        List<Message> history = chatMemoryService.getHistory(sessionId);
        assertEquals(1, history.size());
        assertEquals("Hello", history.get(0).content());
    }

    @Test
    void shouldLimitHistoryToMaxMessages() {
        String sessionId = "session-1";
        for (int i = 0; i < 15; i++) {
            chatMemoryService.addMessage(sessionId, new Message("user", "msg " + i));
        }

        List<Message> history = chatMemoryService.getHistory(sessionId);
        assertEquals(10, history.size());
        assertEquals("msg 5", history.get(0).content());
        assertEquals("msg 14", history.get(9).content());
    }

    @Test
    void shouldReturnEmptyListForNewSession() {
        List<Message> history = chatMemoryService.getHistory("new-session");
        assertTrue(history.isEmpty());
    }
}
