package com.eCommerce.Ecommerce.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Entities.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUserOrderByCreatedAtDesc(User user);

    Wishlist findByUserAndProduct(User user, Product product);

    boolean existsByUserAndProduct(User user, Product product);

    int countByUser(User user);
}
