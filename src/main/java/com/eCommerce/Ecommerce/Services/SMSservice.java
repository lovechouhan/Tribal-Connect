package com.eCommerce.Ecommerce.Services;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Entities.User;
import com.twilio.rest.api.v2010.account.Message;


import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;







  @Service
public class SMSservice {

    @Autowired
    private UserServices userServices;

    @Value("${twilio.account-sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth-token}")
    private String twilioAuthToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    public void sendOTP(String toPhoneNumber, String message) {
        // Logic to send SMS using Twilio or any other SMS service provider

        Twilio.init(twilioAccountSid, twilioAuthToken);
        String phoneNumber = "+" + toPhoneNumber;
        Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioPhoneNumber),
               "Your OTP For Verification To TribalConnect is: " + message
        ).create();
    }

    public void receviedquery(String name, String email, String message) {
        User user = userServices.getUserByEmail(email);
        String phoneNumber = user.getPhoneNumber(); // Assuming your User entity has a getPhoneNumber() method
        Twilio.init(twilioAccountSid, twilioAuthToken);
        Message.creator(
                new PhoneNumber(phoneNumber), // Admin phone number
                new PhoneNumber(twilioPhoneNumber),
               "Query Received from: " + name + ", Email: " + email + ", Message: " + message
        ).create();
    }
  }

    
