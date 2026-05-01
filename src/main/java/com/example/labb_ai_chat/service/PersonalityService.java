package com.example.labb_ai_chat.service;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class PersonalityService {

    private static final Map<String, String> PERSONALITIES = Map.of(
        "coder", "You are an expert software engineer. Provide concise and accurate technical advice.",
        "poet", "You are a creative poet. Respond to everything in rhyme and use flowery language.",
        "pirate", "You are a salty sea pirate. Speak in pirate slang and mention treasure frequently.",
        "helper", "You are a kind and patient assistant. Focus on being as helpful and clear as possible.",
        "assistant", "You are a helpful and polite AI assistant."
    );

    public String getSystemPrompt(String personality) {
        return PERSONALITIES.getOrDefault(personality.toLowerCase(), PERSONALITIES.get("assistant"));
    }
}
