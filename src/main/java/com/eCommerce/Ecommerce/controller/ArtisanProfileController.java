package com.eCommerce.Ecommerce.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eCommerce.Ecommerce.Entities.ArtisanProfile;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Services.ArtisanProfileService;
import com.eCommerce.Ecommerce.Services.CloudinaryImageService;
import com.eCommerce.Ecommerce.Services.SellerService;

@Controller
@RequestMapping("/sellers/artisan-profile")
public class ArtisanProfileController {

    @Autowired
    private ArtisanProfileService artisanProfileService;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private CloudinaryImageService imageService;

    @GetMapping
    public String showProfileForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Seller seller = sellerService.getSellerByEmail(email);

        if (seller == null) {
            return "redirect:/login";
        }

        Optional<ArtisanProfile> existingProfile = artisanProfileService.getProfileBySeller(seller);
        ArtisanProfile profile = existingProfile.orElse(new ArtisanProfile());
        
        model.addAttribute("profile", profile);
        return "seller/artisan-profile";
    }

    @PostMapping
    public String saveProfile(@ModelAttribute("profile") ArtisanProfile profile,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Seller seller = sellerService.getSellerByEmail(email);

            if (seller == null) {
                return "redirect:/login";
            }

            Optional<ArtisanProfile> existingOpt = artisanProfileService.getProfileBySeller(seller);
            if (existingOpt.isPresent()) {
                ArtisanProfile existing = existingOpt.get();
                existing.setArtisanName(profile.getArtisanName());
                existing.setTribeOrCommunity(profile.getTribeOrCommunity());
                existing.setRegionOrState(profile.getRegionOrState());
                existing.setCraftSpecialty(profile.getCraftSpecialty());
                existing.setYearsOfExperience(profile.getYearsOfExperience());
                existing.setShortBio(profile.getShortBio());
                existing.setHandcraftedProcess(profile.getHandcraftedProcess());
                profile = existing;
            } else {
                profile.setSeller(seller);
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                Map data = imageService.upload(imageFile);
                String fileURL = data.get("secure_url") != null ? data.get("secure_url").toString() : data.get("url").toString();
                profile.setArtisanImage(fileURL);
            }

            artisanProfileService.saveOrUpdateProfile(profile);
            redirectAttributes.addFlashAttribute("message", "Artisan profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
        }

        return "redirect:/sellers/artisan-profile";
    }
}
