package com.eCommerce.Ecommerce.helper;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.eCommerce.Ecommerce.Config.Helper;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Services.UserServices;
import com.eCommerce.Ecommerce.controller.UserController;

@ControllerAdvice
@Component
public class helperofproject {

    private static final Logger logger = getLogger(UserController.class);

    @Autowired
    private UserServices uzerServices;
    @Autowired
    private com.eCommerce.Ecommerce.Repo.UserRepo userRepository;

    @Autowired
    private com.eCommerce.Ecommerce.Services.WishlistService wishlistService;

    // For Showing Every Details of User on every page
    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

       if(authentication == null) return;
    
        System.out.println("Adding logged in user information to model");
        String username = Helper.getEmailofLoggedInUser(authentication);
        logger.info("Logged in user email: {}", username);
        
        User user = uzerServices.getUserByEmail(username);
        if (user != null) {
            String userRole = user.getRole();
            System.out.println("user's role: "+userRole);
            System.out.println("user 's info "+user);
            System.out.println("user's name: "+user.getName());
            System.out.println("user's email: "+user.getEmail());
            model.addAttribute("loggedInUser", user);  // user ki key ""loggedInUser"" hai
            model.addAttribute("userRole", userRole);
            model.addAttribute("wishlistCount", wishlistService.getWishlistCount(user));
            model.addAttribute("wishlistedProductIds", wishlistService.getWishlistedProductIds(user));
        }
    }
}
