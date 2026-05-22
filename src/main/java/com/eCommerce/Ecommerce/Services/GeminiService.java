package com.eCommerce.Ecommerce.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Deprecated
public class GeminiService {

    // private final String API_KEY = "AIzaSyBXZ2wPw_5GWwg-_USwOhbLlSX0dzZWke8"; // ⚠️ apni key lagao
    // private final String MODEL = "gemini-2.5-flash"; // ya gemini-1.5-flash

    @Value("${google.api.key}")
    private String API_KEY;

    @Value("${google.api.model}")
    private String MODEL;

    private final WebClient webClient;

      private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiService() {
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

    // ✅ Short summary
    public String generateProductSummary2(String name, String category, String brand, String color, String review, LocalDate createDate) {
        String prompt = String.format("""
            Write a 1–2 line catchy summary for this product:
            Name: %s
            Category: %s
            Brand: %s
            Color: %s
            Review: %s
            Date: %s
        """, name, category, brand, color, review, createDate);

        return callGeminiAPI(prompt);
    }

    // ✅ Detailed description
    public String generateProductDescription2(String name, String category, String brand, String color, String review, LocalDate createDate) {
        String prompt = String.format("""
            Write a detailed and engaging 3–4 line description for this product:
            Name: %s
            Category: %s
            Brand: %s
            Color: %s
            Review: %s
            Date: %s
        """, name, category, brand, color, review, createDate);

        return callGeminiAPI(prompt);
    }
}

