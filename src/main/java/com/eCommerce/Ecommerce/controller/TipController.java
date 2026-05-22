package com.eCommerce.Ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Repo.UserRepo;
import com.eCommerce.Ecommerce.Services.ProductService;
import com.eCommerce.Ecommerce.Services.TipService;

@Controller
@RequestMapping("/user/tip")
public class TipController {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private TipService tipService;

    @PostMapping("/send")
    public String sendTip(
            @ModelAttribute("productId") Long productId,
            @ModelAttribute("amount") int amount,
            @ModelAttribute("message") String message,
            RedirectAttributes redirectAttributes
    ) {
        // ── Resolve logged-in user (same pattern as ReviewController) ──
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            email = auth.getName();
        }

        User user = userRepository.findByEmail(email);
        if (user == null) return "redirect:/login";

        // ── Resolve product and artisan ──
        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                redirectAttributes.addFlashAttribute("tipError", "Product not found.");
                return "redirect:/user/products/view/" + productId;
            }

            Seller artisan = product.getSeller();
            if (artisan == null) {
                redirectAttributes.addFlashAttribute("tipError", "Artisan not found for this product.");
                return "redirect:/user/products/view/" + productId;
            }

            // ── Save the tip ──
            tipService.saveTip(amount, message, user, artisan, product);

            redirectAttributes.addFlashAttribute("tipSuccess",
                    "🙏 Thank you for supporting " + (artisan.getSellerName() != null ? artisan.getSellerName() : "this artisan") + "! Your ₹" + amount + " appreciation means the world.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("tipError", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("tipError", "Something went wrong. Please try again.");
        }

        return "redirect:/user/products/view/" + productId;
    }
}
