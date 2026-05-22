package com.eCommerce.Ecommerce.Services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Domain.AccountStatus;
import com.eCommerce.Ecommerce.Domain.UserRoles;
import com.eCommerce.Ecommerce.Entities.Address;
import com.eCommerce.Ecommerce.Entities.BussinessDetails;
import com.eCommerce.Ecommerce.Entities.BankDetails;
import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Form.SellerForm;
import com.eCommerce.Ecommerce.Repo.AddressRepo;
import com.eCommerce.Ecommerce.Repo.ProductRepo;
import com.eCommerce.Ecommerce.Repo.SellerAddressRepo;
import com.eCommerce.Ecommerce.Repo.SellerRepo;
import com.eCommerce.Ecommerce.Services.CloudinaryImageService;
import com.eCommerce.Ecommerce.Services.SMSservice;
import jakarta.mail.MessagingException;

@Service
public class SellerService {

    @Autowired
    private SellerRepo sellerRepo;

    @Autowired
    private CloudinaryImageService imageService;

    @Autowired
    private SMSservice smsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SellerAddressRepo sellerAddressRepo;

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private VerificationCodeService verificationService;

    public Seller getSellerProfile(String email) {
        return getSellerByEmail(email);
    }

    public long getTotalSellers() {
        return sellerRepo.count();
    }

    public Seller saveSeller(Seller seller) {
        // Validate seller data
        if (seller.getEmail() == null || seller.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        // Check if seller already exists
        Seller existingSeller = sellerRepo.findByEmail(seller.getEmail());
        if (existingSeller != null) {
            throw new IllegalArgumentException("Seller with this email already exists");
        }

        // Set initial account status and role
        seller.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
        seller.setEnabled(false);
        seller.setIsemailVerified(false);
        seller.setRole(UserRoles.SELLER);
        seller.setPassword(passwordEncoder.encode(seller.getPassword()));

        // Handle the pickup address
        Address pickupAddress = seller.getPickupAddress();
        if (pickupAddress != null) {
            // Save the address first
            pickupAddress = addressRepo.save(pickupAddress);
            // Update the seller with saved address
            seller.setPickupAddress(pickupAddress);
        }

        // Save the seller
        Seller savedSeller = sellerRepo.save(seller);
        System.out.println("DEBUG [saveSeller]: Seller saved to DB. ID: " + savedSeller.getId());
        return savedSeller;
    }

    public Seller getSellerById(Long id) {
        return sellerRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found with id: " + id));
    }

    public Seller getSellerByEmail(String email) {
        Seller seller = sellerRepo.findByEmail(email);
        if (seller == null) {
            throw new IllegalArgumentException("Seller not found with email: " + email);
        }
        return seller;
    }

    public List<Seller> getAllSellers() {
        return sellerRepo.getAllSellers();
    }

    public Seller updateSeller(Long id, SellerForm sellerForm) {
        try {
            Seller existingSeller = getSellerById(id);

            // Update fields
            existingSeller.setSellerName(sellerForm.getSellerName());
            existingSeller.setPhoneNumber(sellerForm.getPhoneNumber());
            existingSeller.setPickupAddress(sellerForm.getPickupAddress());
            existingSeller.setBankDetails(sellerForm.getBankDetails());
            existingSeller.setBussinessDetails(sellerForm.getBussinessDetails());

            existingSeller.setGSTIN(sellerForm.getGSTIN());

            if (sellerForm.getProfilePic() != null && !sellerForm.getProfilePic().isEmpty()
                    && existingSeller.getProfileImageUrl() != null && !existingSeller.getProfileImageUrl().isEmpty()) {
                Map data = imageService.upload(sellerForm.getProfilePic());
                String fileURL = data.get("url").toString();
                existingSeller.setProfileImageUrl(fileURL);
            } else {
                existingSeller.setProfileImageUrl(existingSeller.getProfileImageUrl());
            }
            return sellerRepo.save(existingSeller);

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

    }

    public void deleteSeller(Long id) {
        if (!sellerRepo.existsById(id)) {
            throw new IllegalArgumentException("Seller not found with id: " + id);
        }
        sellerRepo.deleteById(id);
    }

    public Seller verifyEmail(String email, String otp) {
        Seller seller = getSellerByEmail(email);
        seller.setIsemailVerified(true);
        return sellerRepo.save(seller);

    }

    public Seller updateSellerAccountStatus(Long sellerId, AccountStatus status) throws MessagingException {
        Seller seller = getSellerById(sellerId);
        seller.setAccountStatus(status);

        String message = "Your seller account status has been updated to: " + status;

        smsService.sendOTP(seller.getPhoneNumber(), message);
        ;
        return sellerRepo.save(seller);
    }

    public List<Product> getAllProducts() {

        return productRepo.findAll();
    }

    public Seller getSellerByName(String name) {
        Seller seller = sellerRepo.findSellerByName(name);
        if (seller == null) {
            throw new IllegalArgumentException("Seller not found with name: " + name);
        }
        return seller;
    }

    public void updateSellerStatus(Seller seller) {
        sellerRepo.save(seller);
    }
}
