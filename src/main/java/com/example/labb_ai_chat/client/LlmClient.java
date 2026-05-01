package com.example.labb_ai_chat.client;

import com.example.labb_ai_chat.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
            retryFor = { WebClientResponseException.class },
            noRetryFor = { NonRetryableException.class },
            maxAttempts = 4,
            backoff = @Backoff(delay = 1)
    )
    public String getCompletion(List<Message> messages) {
        try {
            return doCall(messages);

        } catch (WebClientResponseException ex) {

            if (!isRetryable(ex)) {
                throw new NonRetryableException("Non-retryable HTTP error", ex);
            }

            int attempt = getRetryCount();
            long delay = resolveDelay(ex, attempt);

            logRetry(ex, attempt, delay);
            sleep(delay);

            throw ex;
        }
    }

    private String doCall(List<Message> messages) {
        OpenAiRequest request = new OpenAiRequest(model, messages);

        OpenAiResponse response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAiResponse.class)
                .block();

        if (response != null && !response.choices().isEmpty()) {
            return response.choices().get(0).message().content();
        }

        throw new RuntimeException("Empty response from API");
    }

    private boolean isRetryable(WebClientResponseException ex) {
        int status = ex.getStatusCode().value();
        return status == 429 || (status >= 500 && status <= 504);
    }

    private int getRetryCount() {
        var context = RetrySynchronizationManager.getContext();
        return (context != null) ? context.getRetryCount() : 0;
    }

    private long resolveDelay(WebClientResponseException ex, int attempt) {

        String retryAfter = ex.getHeaders().getFirst("Retry-After");
        if (retryAfter != null) {
            try {
                return Long.parseLong(retryAfter) * 1000;
            } catch (NumberFormatException ignored) {
            }
        }

        long baseDelay = (long) (500 * Math.pow(2, attempt));

        long jitter = ThreadLocalRandom.current().nextLong(0, 500);

        long maxDelay = 5000;

        return Math.min(baseDelay + jitter, maxDelay);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private void logRetry(WebClientResponseException ex, int attempt, long delay) {
        System.out.printf(
                "Retry #%d in %d ms بسبب status %d%n",
                attempt,
                delay,
                ex.getStatusCode().value()
        );
    }


    @Recover
    public String recover(WebClientResponseException ex, List<Message> messages) {
        return "Tjänsten är tillfälligt överbelastad. Försök igen om en stund.";
    }

    @Recover
    public String recover(NonRetryableException ex, List<Message> messages) {
        throw ex;
    }


    static class NonRetryableException extends RuntimeException {
        public NonRetryableException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }
}