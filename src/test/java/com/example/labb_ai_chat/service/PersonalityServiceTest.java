package com.example.labb_ai_chat.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PersonalityServiceTest {

    private final PersonalityService personalityService = new PersonalityService();

    @Test
    void shouldReturnCorrectPromptForKnownPersonality() {
        String prompt = personalityService.getSystemPrompt("coder");
        assertTrue(prompt.contains("expert software engineer"));
    }

    @Test
    void shouldReturnDefaultPromptForUnknownPersonality() {
        String prompt = personalityService.getSystemPrompt("unknown");
        assertTrue(prompt.contains("helpful and polite AI assistant"));
    }

    @Test
    void shouldBeCaseInsensitive() {
        String prompt = personalityService.getSystemPrompt("CODER");
        assertTrue(prompt.contains("expert software engineer"));
    }
}
