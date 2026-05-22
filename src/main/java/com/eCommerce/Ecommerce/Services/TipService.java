package com.eCommerce.Ecommerce.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Entities.Tip;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Repo.TipRepo;

@Service
public class TipService {

    @Autowired
    private TipRepo tipRepo;

    /**
     * Save a new tip (appreciation) from a user to an artisan via a product.
     */
    public Tip saveTip(int amount, String message, User user, Seller artisan, Product product) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Tip amount must be positive");
        }
        if (amount > 10000) {
            throw new IllegalArgumentException("Tip amount cannot exceed ₹10,000");
        }

        Tip tip = new Tip();
        tip.setAmount(amount);
        tip.setMessage(message != null ? message : "");
        tip.setUser(user);
        tip.setArtisan(artisan);
        tip.setProduct(product);

        return tipRepo.save(tip);
    }

    /**
     * Get total amount of tips received by an artisan.
     */
    public int getTotalTipsForArtisan(Long artisanId) {
        return tipRepo.getTotalTipAmountByArtisanId(artisanId);
    }

    /**
     * Get the number of unique supporters for an artisan.
     */
    public int getUniqueSupportersForArtisan(Long artisanId) {
        return tipRepo.getUniqueSupportersByArtisanId(artisanId);
    }

    /**
     * Get total tip amount for a specific product.
     */
    public int getTotalTipsForProduct(Long productId) {
        return tipRepo.getTotalTipAmountByProductId(productId);
    }

    /**
     * Get the number of tips for a specific product.
     */
    public int getTipCountForProduct(Long productId) {
        return tipRepo.getTipCountByProductId(productId);
    }

    /**
     * Get all tips by a user.
     */
    public List<Tip> getTipsByUser(Long userId) {
        return tipRepo.findByUserId(userId);
    }
}
