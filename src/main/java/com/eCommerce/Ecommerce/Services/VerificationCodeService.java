package com.eCommerce.Ecommerce.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Repo.SellerRepo;

@Service
public class VerificationCodeService {

    @Autowired
    private UserServices userServices;
    @Autowired
    private SMSservice smsService;
    @Autowired
    private SellerRepo sellerRepo;

    public boolean verifyOtp(String email, int otp) {
        User user = userServices.getUserByEmail(email);
        if (user != null) {
            return user.getOtp() == otp;
        }
        return false;
    }

    public int generateAndSendOtp(String email) {
        // BREAKPOINT: Check the email being passed.
        System.out.println("DEBUG [generateAndSendOtp]: Starting OTP generation for email: " + email);

        // Logic to generate OTP and send it via email
        int otp = (int) (Math.random() * 9000) + 1000; // Generate a 4-digit OTP

        System.out.println("Generated OTP: " + otp); // For testing purposes, print the OTP to the console

        // Fetch the user by email
        User user = userServices.getUserByEmail(email);
        System.out.println("DEBUG [generateAndSendOtp]: Fetched user? " + (user != null));

        Seller seller = sellerRepo.findByEmail(email);
        System.out.println("DEBUG [generateAndSendOtp]: Fetched seller? " + (seller != null));

        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10);

        String phoneNumber = null;
        if (user != null) {
            phoneNumber = user.getPhoneNumber();
            System.out.println("DEBUG [generateAndSendOtp]: User phone number: " + phoneNumber);

            user.setOtp(otp);
            user.setOtpExpiryTime(expiryTime);
            userServices.updateUserstatus(user);
        }

        if (seller != null) {
            if (phoneNumber == null)
                phoneNumber = seller.getPhoneNumber();
            seller.setOtp(otp);
            seller.setOtpExpiryTime(expiryTime);
            sellerRepo.save(seller);
        }

        // n8n Email Webhook Integration
        try {
            RestTemplate restTemplate = new RestTemplate();

            java.util.Map<String, String> body = new java.util.HashMap<>();
            body.put("token", "MY_SECRET");
            body.put("email", email);
            body.put("otp", String.valueOf(otp));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<java.util.Map<String, String>> request = new HttpEntity<>(body, headers);

            System.out.println("DEBUG [generateAndSendOtp]: Sending OTP to n8n webhook for email: " + email);
            
            org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://love04.app.n8n.cloud/webhook/send-otp",
                    request,
                    String.class
            );

            System.out.println("DEBUG [generateAndSendOtp]: Webhook response: " + response.getBody());
        } catch (Exception e) {
            System.out.println("DEBUG [generateAndSendOtp]: Failed to trigger n8n webhook: " + e.getMessage());
        }

        return otp;
    }

}
