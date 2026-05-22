package com.eCommerce.Ecommerce.Services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Domain.UserRoles;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Form.SellerForm;
import com.eCommerce.Ecommerce.Form.UserForm;
import com.eCommerce.Ecommerce.Repo.CartRepo;
import com.eCommerce.Ecommerce.Repo.OrderRepo;
import com.eCommerce.Ecommerce.Repo.SellerRepo;
import com.eCommerce.Ecommerce.Repo.UserRepo;
import com.eCommerce.Ecommerce.Repo.VerificationCodeRepo;

@Service
public class UserServices {

    @Autowired
    UserRepo userRepo;

    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SellerRepo SellerRepo;

    @Autowired
    private VerificationCodeRepo verificationCodeRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private CloudinaryImageService imageService;

    public long getTotalUsers() {
        return userRepo.count();
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User saveUser(User user) {

        String inputRole = user.getRole();

        if (inputRole == UserRoles.USER) {
            User u1 = userRepo.findByEmail(user.getEmail());
            if (u1 != null) {
                if (!u1.isEnabled()) {
                    throw new IllegalArgumentException("UNVERIFIED");
                }
                throw new IllegalArgumentException("User with this email already exists");
            }
            user.setRole(UserRoles.USER);
        } else if (inputRole == UserRoles.SELLER) {
            Seller s1 = SellerRepo.findByEmail(user.getEmail());
            if (s1 != null) {
                if (!s1.isEnabled()) {
                    throw new IllegalArgumentException("UNVERIFIED");
                }
                throw new IllegalArgumentException("User with this email already exists");
            }
            user.setRole(UserRoles.SELLER);
        } else {
            throw new IllegalArgumentException("Invalid role: " + inputRole);
        }

        return userRepo.save(user);
    }

    public void sendOTP(String email) {
        String SIGNING_PREFIX = "signin_";
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public boolean updateUser(UserForm userinfo) {
        User u1 = userRepo.findByEmail(userinfo.getEmail());
        if (u1 == null)
            return false;
        try {
            u1.setEmail(userinfo.getEmail());
            u1.setName(userinfo.getName());
            u1.setPhoneNumber(userinfo.getPhoneNumber());
            if (u1.getProfileImageUrl() != null && !u1.getProfileImageUrl().isEmpty()
                    && userinfo.getProfilePic() != null && !userinfo.getProfilePic().isEmpty()) {

                Map data = imageService.upload(userinfo.getProfilePic());
                String fileURL = data.get("url").toString();
                u1.setProfileImageUrl(fileURL);
            } else {
                u1.setProfileImageUrl(u1.getProfileImageUrl());
            }
            userRepo.save(u1);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public User findByOTP(int otp) {
        return userRepo.findByOtp(otp);
    }

    public int getProductCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;
        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            email = oauthToken.getPrincipal().getAttribute("email");
        } else {
            email = auth.getName();
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        int count = orderRepo.countProductsByUserId(user.getId());
        if (count == 0) {
            return 0;
        }
        return count;
    }

    public int getTotalSpendings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;
        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            email = oauthToken.getPrincipal().getAttribute("email");
        } else {
            email = auth.getName();
        }

        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Double sum = (double) orderRepo.sumTotalSpendingsByUserId(user.getId());
        return (int) Math.round(sum != null ? sum : 0.0);
    }

    public User findById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    public void updateUserstatus(User user) {
        userRepo.save(user);
    }

    public void updateUserandSeller(SellerForm sellerinfo) {
        User u1 = userRepo.findByEmail(sellerinfo.getEmail());
        if (u1 == null)
            return;
        try {
            u1.setEmail(sellerinfo.getEmail());
            u1.setName(sellerinfo.getSellerName());
            u1.setPhoneNumber(sellerinfo.getPhoneNumber());
            if (u1.getProfileImageUrl() != null && !u1.getProfileImageUrl().isEmpty()
                    && sellerinfo.getProfilePic() != null && !sellerinfo.getProfilePic().isEmpty()) {

                Map data = imageService.upload(sellerinfo.getProfilePic());
                String fileURL = data.get("url").toString();
                u1.setProfileImageUrl(fileURL);
            } else {
                u1.setProfileImageUrl(u1.getProfileImageUrl());
            }
            userRepo.save(u1);

        } catch (Exception e) {
            e.printStackTrace();
    }

}
}