package com.eCommerce.Ecommerce.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Repo.ProductRepo;
import com.eCommerce.Ecommerce.Services.AIProductService;



@RestController
@RequestMapping("/api/ai-summary")
public class ProductAIController {

    private static final Logger log = LoggerFactory.getLogger(ProductAIController.class);

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private AIProductService aiProductService;

    @PostMapping("/{id}")
    public Map<String, String> generateProductSummary(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Null-safe extraction of fields
        String name = product.getName() != null ? product.getName() : "";
        String category = product.getCategory() != null ? product.getCategory() : "";
        String brand = product.getBrand() != null ? product.getBrand() : "";
        String color = product.getColor() != null ? product.getColor() : "";
        String reviews = product.getReviews() != null ? product.getReviews().toString() : "";
        LocalDate createdDate = product.getCreatedAt() != null ? product.getCreatedAt().toLocalDate() : LocalDate.now();

        String summary;
        String description;
        try {
            summary = aiProductService.generateProductSummary(
                    name, category, brand, color, reviews, createdDate);
        } catch (Exception ex) {
            log.warn("AI summary generation failed for product {}: {}", id, ex.toString());
            summary = "AI service is currently unavailable. Showing a basic summary.\n" +
                    String.format("%s (%s, %s, %s). Reviews: %s", name, category, brand, color,
                            reviews != null && !reviews.isBlank() ? "available" : "not available");
        }

        try {
            description = aiProductService.generateProductDescription(
                    name, category, brand, color, reviews, createdDate);
        } catch (Exception ex) {
            log.warn("AI description generation failed for product {}: {}", id, ex.toString());
            description = "AI description is unavailable. Please try again later.";
        }

        model.addAttribute("product", product);
        Map<String, String> response = new HashMap<>();
        response.put("summary", summary);
        response.put("description", description);
        return response;
    }
}
