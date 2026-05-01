package com.example.labb_ai_chat.client;

import com.example.labb_ai_chat.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
public class LlmClient {

    private final WebClient webClient;
    private final String model;

    public LlmClient(WebClient.Builder webClientBuilder,
                     @Value("${openai.api.key:${OPENAI_API_KEY:}}") String apiKey,
                     @Value("${openai.api.url:https://openrouter.ai/api/v1}") String baseUrl,
                     @Value("${openai.model:google/gemini-2.0-flash-001}") String model) {
        this.webClient = webClientBuilder
            .baseUrl(baseUrl)
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .build();
        this.model = model;
    }

    @Retryable(
        retryFor = { WebClientResponseException.TooManyRequests.class, WebClientResponseException.ServiceUnavailable.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public String getCompletion(List<Message> messages) {
        OpenAiRequest request = new OpenAiRequest(model, messages);

        OpenAiResponse response = webClient.post()
            .uri("/chat/completions")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(OpenAiResponse.class)
            .block(); // Synchronous block as per architecture decision

        if (response != null && !response.choices().isEmpty()) {
            return response.choices().get(0).message().content();
        }
        throw new RuntimeException("Empty response from OpenAI");
    }
}
