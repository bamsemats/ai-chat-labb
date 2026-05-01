package com.example.labb_ai_chat.client;

import com.example.labb_ai_chat.model.Message;
import java.util.List;

public record OpenAiRequest(String model, List<Message> messages) {
}
