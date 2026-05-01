package com.example.labb_ai_chat.client;

import com.example.labb_ai_chat.model.Message;
import java.util.List;

public record OpenAiResponse(List<Choice> choices) {
    public record Choice(Message message) {
    }
}
