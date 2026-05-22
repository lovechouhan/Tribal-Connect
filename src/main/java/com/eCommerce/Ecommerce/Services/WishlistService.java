package com.eCommerce.Ecommerce.Services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Entities.Wishlist;
import com.eCommerce.Ecommerce.Repo.WishlistRepository;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepo;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    public List<Wishlist> getUserWishlist(User user) {
        return wishlistRepo.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public boolean addToWishlist(User user, Long productId) {
        Product product = productService.getProductById(productId);
        if (product != null) {
            if (!wishlistRepo.existsByUserAndProduct(user, product)) {
                Wishlist wishlist = new Wishlist();
                wishlist.setUser(user);
                wishlist.setProduct(product);
                wishlistRepo.save(wishlist);
                return true; // Added successfully
            }
        }
        return false; // Already exists or product not found
    }

    @Transactional
    public boolean removeFromWishlist(User user, Long productId) {
        Product product = productService.getProductById(productId);
        if (product != null) {
            Wishlist wishlist = wishlistRepo.findByUserAndProduct(user, product);
            if (wishlist != null) {
                wishlistRepo.delete(wishlist);
                return true; // Removed successfully
            }
        }
        return false;
    }

    @Transactional
    public boolean removeWishlistItem(User user, Long wishlistId) {
        Wishlist item = wishlistRepo.findById(wishlistId).orElse(null);
        if (item != null && item.getUser().getId().equals(user.getId())) {
            wishlistRepo.delete(item);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean moveToCart(User user, Long wishlistId) {
        Wishlist item = wishlistRepo.findById(wishlistId).orElse(null);
        if (item != null && item.getUser().getId().equals(user.getId())) {
            cartService.addToCart(user, item.getProduct());
            wishlistRepo.delete(item);
            return true;
        }
        return false;
    }

    public int getWishlistCount(User user) {
        if (user == null) return 0;
        return wishlistRepo.countByUser(user);
    }

    public Set<Long> getWishlistedProductIds(User user) {
        if (user == null) return Set.of();
        return wishlistRepo.findByUserOrderByCreatedAtDesc(user).stream()
                .map(w -> w.getProduct().getId())
                .collect(Collectors.toSet());
    }
}
