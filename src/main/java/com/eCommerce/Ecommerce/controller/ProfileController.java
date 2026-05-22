package com.eCommerce.Ecommerce.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Form.UserForm;
import com.eCommerce.Ecommerce.Repo.UserRepo;
import com.eCommerce.Ecommerce.Services.ProfileService;
import com.eCommerce.Ecommerce.Services.UserServices;
import com.eCommerce.Ecommerce.dto.SellerProfileDTO;
import com.eCommerce.Ecommerce.dto.UserProfileDTO;

import jakarta.validation.Valid;

@Controller

public class ProfileController {
    @Autowired
    private UserServices userService;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ProfileService profileService;

    // User Profile Endpoints
    @GetMapping("/user")
    public String getUserProfile(Authentication authentication, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }
        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            // For regular login
            email = auth.getName();
        }
        System.out.println("Logged in user Profile kaa mila huaa email: " + email);
        User user = userRepository.findByEmail(email);
        if (user == null)
            return "redirect:/login";

        String role = user.getRole();
        if (!"USER_ROLE".equals(role)) {
            return "error/access-denied";
        }

        Long userId = getUserIdFromAuthentication(authentication);
        UserProfileDTO profile = profileService.getUserProfile(userId);
        model.addAttribute("profile", profile);
        model.addAttribute("isUser", true);
        return "user/profile";
    }

    @GetMapping("/user/profile/updateInfo")
    public String getUserUpdateInfo(@RequestParam("email") String email, Authentication authentication, Model model) {

        User userinfo = userRepository.findByEmail(email);
        if (userinfo == null)
            return "redirect:/login";

        // Pre-populate form with existing user details for a better UX
        UserForm form = new UserForm();
        form.setName(userinfo.getName());
        form.setEmail(userinfo.getEmail());
        form.setPhoneNumber(userinfo.getPhoneNumber());
        // Address-related fields can be mapped here if available
        model.addAttribute("userinfo", form);
        model.addAttribute("profileImageUrl", userinfo.getProfileImageUrl());
        return "user/updateinfo";
    }

    @PostMapping("/user/profile/updateInfo/save")
    public String updateUserProfile(@ModelAttribute("userinfo") UserForm userinfo, Model model, RedirectAttributes redirectAttributes) {

         User user = userRepository.findByEmail(userinfo.getEmail());
         if(user==null) return "redirect:/login";

         boolean isUpdated = userService.updateUser(userinfo);

        if (isUpdated) {
            model.addAttribute("message", "Profile updated successfully");
            redirectAttributes.addFlashAttribute("success", "info");
            redirectAttributes.addFlashAttribute("alertTitle", " Profile Updated Successfully");
            redirectAttributes.addFlashAttribute("alertMessage", "Your profile has been updated successfully.");
            return "redirect:/user/settings/account";

        } else {
            model.addAttribute("error", "Failed to update profile");
            redirectAttributes.addFlashAttribute("error", "error");
            redirectAttributes.addFlashAttribute("alertTitle", " Profile Update Failed");
            redirectAttributes.addFlashAttribute("alertMessage", "There was an error updating your profile. Please try again.");
        }
        return "redirect:/user/settings/account";
    }

    @PostMapping("/user/profile/password")
    public ResponseEntity<?> updateUserPassword(
            Authentication authentication,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        Long userId = getUserIdFromAuthentication(authentication);
        profileService.updateUserPassword(userId, oldPassword, newPassword);
        return ResponseEntity.ok().body("Password updated successfully");
    }

    @PostMapping("/user/profile/picture")
    public ResponseEntity<?> updateUserProfilePicture(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) throws IOException {

        Long userId = getUserIdFromAuthentication(authentication);
        String filename = profileService.uploadUserProfilePicture(userId, file);
        return ResponseEntity.ok().body(filename);
    }












    // Seller Profile Endpoints
    @GetMapping("/seller")
    public String getSellerProfile(Authentication authentication, Model model) {
        Long sellerId = getSellerIdFromAuthentication(authentication);
        SellerProfileDTO profile = profileService.getSellerProfile(sellerId);
        model.addAttribute("profile", profile);
        return "seller/profile";
    }


    @GetMapping("/seller/profile/updateInfo/{email}")
    public String getSellerUpdateInfo(@PathVariable("email") String email, Authentication authentication, Model model) {

        User userinfo = userRepository.findByEmail(email);
        if (userinfo == null)
            return "redirect:/login";

        // Pre-populate form with existing seller details for a better UX
        SellerProfileDTO form = new SellerProfileDTO();
        form.setSellerName(userinfo.getName());
        form.setEmail(userinfo.getEmail());
        form.setPhoneNumber(userinfo.getPhoneNumber());
        // Additional seller-related fields can be mapped here if available
        model.addAttribute("profile", form);
        model.addAttribute("profileImageUrl", userinfo.getProfileImageUrl());
        return "seller/updateinfo";
    }





    @PostMapping("/seller")
    public String updateSellerProfile(
            Authentication authentication,
            @Valid @ModelAttribute("profile") SellerProfileDTO profileDTO,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "seller/profile";
        }

        Long sellerId = getSellerIdFromAuthentication(authentication);
        profileService.updateSellerProfile(sellerId, profileDTO);
        model.addAttribute("message", "Profile updated successfully");
        return "redirect:/profile/seller";
    }

    @PostMapping("/seller/password")
    public ResponseEntity<?> updateSellerPassword(
            Authentication authentication,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        Long sellerId = getSellerIdFromAuthentication(authentication);
        profileService.updateSellerPassword(sellerId, oldPassword, newPassword);
        return ResponseEntity.ok().body("Password updated successfully");
    }

    @PostMapping("/seller/logo")
    public ResponseEntity<?> updateSellerStoreLogo(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) throws IOException {

        Long sellerId = getSellerIdFromAuthentication(authentication);
        String filename = profileService.uploadSellerStoreLogo(sellerId, file);
        return ResponseEntity.ok().body(filename);
    }

    // Helper methods to get IDs from Authentication
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // Implement based on your authentication setup
        return 1L; // Placeholder
    }

    private Long getSellerIdFromAuthentication(Authentication authentication) {
        // Implement based on your authentication setup
        return 1L; // Placeholder
    }
}