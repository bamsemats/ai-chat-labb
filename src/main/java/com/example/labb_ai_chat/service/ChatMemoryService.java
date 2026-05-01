package com.example.labb_ai_chat.service;

import com.example.labb_ai_chat.model.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatMemoryService {

    private static final int MAX_MESSAGES = 10;
    private final Map<String, List<Message>> sessionMemory = new ConcurrentHashMap<>();

    public List<Message> getHistory(String sessionId) {
        return sessionMemory.getOrDefault(sessionId, Collections.emptyList());
    }

    public void addMessage(String sessionId, Message message) {
        sessionMemory.compute(sessionId, (id, history) -> {
            List<Message> newHistory = (history == null) ? new ArrayList<>() : new ArrayList<>(history);
            newHistory.add(message);
            if (newHistory.size() > MAX_MESSAGES) {
                newHistory = newHistory.subList(newHistory.size() - MAX_MESSAGES, newHistory.size());
            }
            return newHistory;
        });
    }

    public void clear(String sessionId) {
        sessionMemory.remove(sessionId);
    }
}
