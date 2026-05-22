package com.eCommerce.Ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.PathVariable;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Form.SellerForm;
import com.eCommerce.Ecommerce.Form.UserForm;
import com.eCommerce.Ecommerce.Services.UserSettingsService;
import com.eCommerce.Ecommerce.dto.SellerSettingsDTO;
import com.eCommerce.Ecommerce.dto.UserSettingsDTO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller

public class SettingController {

    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private com.eCommerce.Ecommerce.Services.SellerService sellerService;

    @Autowired
    private com.eCommerce.Ecommerce.Services.UserServices userServices;

    @Autowired
    private com.eCommerce.Ecommerce.Services.SellerSettingsService sellerSettingsService;

    @GetMapping("/user/settings")
    public String getSettings(Model model) {

        return "user/settings";
    }

    @PostMapping("/user/settings")
    public String updateSettings(@ModelAttribute UserSettingsDTO settings, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        model.addAttribute("settings", settings);
        model.addAttribute("message", "Settings updated successfully");
        return "user/settings";
    }

    @GetMapping("/user/settings/account")
    public String getAccountSettings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            // For regular login
            email = auth.getName();
        }
        User user = userSettingsService.getUserByEmail(email);
        Long userId = user.getId();

        UserSettingsDTO settings = userSettingsService.getUserSettings(email);
        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        model.addAttribute("settings", settings);
        return "user/accountInfo";
    }

    @GetMapping("/user/settings/password")
    public String getPasswordSettings(Model model, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;
        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
            // Use keys consumed by base.html to show SweetAlert
            redirectAttributes.addFlashAttribute("alertType", "info");
            redirectAttributes.addFlashAttribute("alertTitle", "Not Available");
            redirectAttributes.addFlashAttribute("alertMessage",
                    "Changing password is not applicable for your account.");
            return "redirect:/user/settings"; // Redirect OAuth2 users to general settings
        } else {
            // For regular login
            email = auth.getName();
        }
        User user = userSettingsService.getUserByEmail(email);
        model.addAttribute("user", user);
        UserSettingsDTO settings = userSettingsService.getUserSettings(email);
        model.addAttribute("settings", settings);
        return "user/changePassword";
    }

    @GetMapping("/user/settings/privacy")
    public String getPrivacySettings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserSettingsDTO settings = userSettingsService.getUserSettings(email);
        model.addAttribute("settings", settings);
        return "user/privacy-settings";
    }

    @PostMapping("/user/settings/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            Model model, RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;
        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
            // Use keys consumed by base.html to show SweetAlert
            redirectAttributes.addFlashAttribute("alertType", "info");
            redirectAttributes.addFlashAttribute("alertTitle", "Not Available");
            redirectAttributes.addFlashAttribute("alertMessage",
                    "Changing password is not applicable for your OAuth account.");
            return "redirect:/user/settings"; // Redirect OAuth2 users to general settings
        } else {
            // For regular login
            email = auth.getName();
        }

        boolean success = userSettingsService.changePassword(email, currentPassword, newPassword);
        if (success) {
            redirectAttributes.addFlashAttribute("alertType", "success");
            redirectAttributes.addFlashAttribute("alertTitle", "Success!");
            redirectAttributes.addFlashAttribute("alertMessage", "Password changed successfully.");
            model.addAttribute("message", "Password changed successfully");
        } else {
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "Failed!");
            redirectAttributes.addFlashAttribute("alertMessage", "Incorrect current password.");
            model.addAttribute("error", "Incorrect current password");
        }
        return "user/changePassword";
    }

    // seller settings section

    @GetMapping("/seller/settings")
    public String getSellerSettings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            // For regular login
            email = auth.getName();
        }
        Seller seller = sellerSettingsService.getSellerByEmail(email);
        model.addAttribute("seller", seller);

        SellerSettingsDTO settings = sellerSettingsService.getSellerSettings(email);
        model.addAttribute("settings", settings);
        return "seller/settings";
    }

    @GetMapping("/seller/settings/store-info")
    public String getStoreInfo(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            // For regular login
            email = auth.getName();
        }
        Seller seller = sellerSettingsService.getSellerByEmail(email);
        model.addAttribute("seller", seller);

        SellerSettingsDTO settings = sellerSettingsService.getSellerSettings(email);
        model.addAttribute("settings", settings);
        return "seller/storeSettings";
    }

    @GetMapping("/seller/settings/bank-details")
    public String getBankDetails(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            // For regular login
            email = auth.getName();
        }
        Seller seller = sellerSettingsService.getSellerByEmail(email);
        model.addAttribute("seller", seller);

        SellerSettingsDTO settings = sellerSettingsService.getSellerSettings(email);
        model.addAttribute("settings", settings);
        return "seller/sellerBank";
    }

    @GetMapping("/seller/settings/updateInfo")
    public String getUpdateSellerInfo(Model model, @RequestParam("email") String email) {
        Seller sellerinfo = sellerSettingsService.getSellerByEmail(email);
        if (sellerinfo == null)
            return "redirect:/login";
        SellerForm sellerForm = new SellerForm();
        sellerForm.setEmail(sellerinfo.getEmail());
        sellerForm.setSellerName(sellerinfo.getSellerName());
        sellerForm.setPhoneNumber(sellerinfo.getPhoneNumber());
        sellerForm.setGSTIN(sellerinfo.getGSTIN());
        sellerForm.setPickupAddress(sellerinfo.getPickupAddress());
        sellerForm.setBankDetails(sellerinfo.getBankDetails());
        sellerForm.setBussinessDetails(sellerinfo.getBussinessDetails());
        sellerForm.setProfileImageUrl(sellerinfo.getProfileImageUrl());
        model.addAttribute("sellerForm", sellerForm);
        // model.addAttribute("profileImageUrl", sellerinfo.getProfileImageUrl());
        return "seller/sellerUpdation";
    }

    @PostMapping("/seller/settings/updateInfo")
    public String updateSellerSettings(@ModelAttribute("sellerForm") SellerForm sellerinfo, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            // Prefer authenticated user's email over form value
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = null;
            if (auth instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) auth;
                OAuth2User oauth2User = oauthToken.getPrincipal();
                email = oauth2User.getAttribute("email");
            } else if (auth != null) {
                email = auth.getName();
            }
            if (email == null || email.isEmpty()) {
                email = sellerinfo.getEmail();
            }

            Seller seller = sellerService.getSellerByEmail(email);
            User user = userSettingsService.getUserByEmail(email);
            if (user == null || seller == null) {
                return "redirect:/login";
            }

            userServices.updateUserandSeller(sellerinfo);

            seller = sellerService.updateSeller(seller.getId(), sellerinfo);
            model.addAttribute("seller", seller);
            model.addAttribute("message", "Settings updated successfully");
            redirectAttributes.addFlashAttribute("alertType", "success");
            redirectAttributes.addFlashAttribute("alertTitle", "Success!");
            redirectAttributes.addFlashAttribute("alertMessage", "Settings updated successfully.");
            return "redirect:/seller/settings/store-info";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while updating settings.");
            redirectAttributes.addFlashAttribute("alertType", "error");
            redirectAttributes.addFlashAttribute("alertTitle", "Error!");
            redirectAttributes.addFlashAttribute("alertMessage", "An error occurred while updating settings.");
            return "redirect:/seller/settings/store-info";
        }

    }
}