package com.eCommerce.Ecommerce.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Entities.Wishlist;
import com.eCommerce.Ecommerce.Repo.UserRepo;
import com.eCommerce.Ecommerce.Services.WishlistService;

@Controller
@RequestMapping("/user/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private UserRepo userRepository;

    private User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        String email;
        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            email = auth.getName();
        }
        return userRepository.findByEmail(email);
    }

    @GetMapping
    public String viewWishlistPage(Model model) {
        User user = getLoggedInUser();
        if (user == null) {
            return "redirect:/login";
        }
        List<Wishlist> wishlistItems = wishlistService.getUserWishlist(user);
        model.addAttribute("wishlistItems", wishlistItems);
        return "user/wishlist";
    }

    @PostMapping("/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleWishlistAjax(@RequestParam("productId") Long productId) {
        User user = getLoggedInUser();
        Map<String, Object> response = new HashMap<>();
        if (user == null) {
            response.put("status", "unauthorized");
            response.put("message", "Please login to add to wishlist.");
            return ResponseEntity.status(401).body(response);
        }

        boolean added = wishlistService.addToWishlist(user, productId);
        if (!added) {
            // If not added, it means it already exists, so let's remove it
            wishlistService.removeFromWishlist(user, productId);
            response.put("status", "removed");
            response.put("message", "Removed from Wishlist");
        } else {
            response.put("status", "added");
            response.put("message", "Added to Wishlist");
        }
        response.put("wishlistCount", wishlistService.getWishlistCount(user));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove/{wishlistId}")
    public String removeWishlistItem(@PathVariable("wishlistId") Long wishlistId, RedirectAttributes redirectAttributes) {
        User user = getLoggedInUser();
        if (user == null) return "redirect:/login";

        boolean removed = wishlistService.removeWishlistItem(user, wishlistId);
        if (removed) {
            redirectAttributes.addFlashAttribute("alertType", "success");
            redirectAttributes.addFlashAttribute("alertTitle", "Removed");
            redirectAttributes.addFlashAttribute("alertMessage", "Item removed from your wishlist.");
        }
        return "redirect:/user/wishlist";
    }

    @PostMapping("/move-to-cart/{wishlistId}")
    public String moveWishlistItemToCart(@PathVariable("wishlistId") Long wishlistId, RedirectAttributes redirectAttributes) {
        User user = getLoggedInUser();
        if (user == null) return "redirect:/login";

        boolean moved = wishlistService.moveToCart(user, wishlistId);
        if (moved) {
            redirectAttributes.addFlashAttribute("alertType", "success");
            redirectAttributes.addFlashAttribute("alertTitle", "Moved to Cart");
            redirectAttributes.addFlashAttribute("alertMessage", "Item successfully moved to your shopping cart.");
        } else {
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "Error");
            redirectAttributes.addFlashAttribute("alertMessage", "Could not move item to cart.");
        }
        return "redirect:/user/wishlist";
    }
}
