package com.eCommerce.Ecommerce.Services;

import java.time.LocalDate;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;


@Service
public class AIProductService {

    private final ChatClient chatClient;

    public AIProductService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
  
    }
   
    public String generateProductSummary(String name, String category, String brand, String color, String review, LocalDate createDate) {
        String prompt = String.format("""
            Create a short (1–2 lines) e-commerce marketing summary for this product:
            Product: %s
            Category: %s
            Brand: %s
            Color: %s
            Review: %s
            createDate: %s

        """, name, category, brand, color,review, createDate);

        
        return chatClient.prompt(prompt).call().content();
    }

    public String generateProductDescription(String name, String category, String brand, String color, String review, LocalDate createDate) {
        String prompt = String.format("""
            Write a detailed and engaging e-commerce product description (3–4 lines):
            Product: %s
            Category: %s
            Brand: %s
            Color: %s
            Review: %s
            createDate: %s
        """, name, category, brand, color,review, createDate);

        return chatClient.prompt(prompt).call().content();
    }
}
