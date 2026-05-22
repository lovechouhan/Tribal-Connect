package com.eCommerce.Ecommerce.Services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;

@Service
@Deprecated
public class GeminiChatService {
    

    
    @Value("${google.api.key}")
    private String API_KEY;

    @Value("${google.api.model}")
    private String MODEL;

    private final WebClient webClient;

      private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiChatService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models")
                .build();
    }

    // ✅ Common API call — returns only the text part
    private String callGeminiAPI(String prompt) {
        try {
            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
                )
            );

            String response = webClient.post()
                    .uri("/" + MODEL + ":generateContent?key=" + API_KEY)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // ✅ Extract only the AI text from the JSON
            JsonNode json = objectMapper.readTree(response);
            return json.path("candidates").get(0)
                       .path("content").path("parts").get(0)
                       .path("text").asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error while generating AI response: " + e.getMessage();
        }
    }
    

    public Flux<String> getChatResponses2(String inputText) {
        Flux<String> response = Flux.just(callGeminiAPI(inputText));
         return response;
    }
}
