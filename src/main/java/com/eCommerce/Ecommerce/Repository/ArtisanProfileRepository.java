package com.eCommerce.Ecommerce.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eCommerce.Ecommerce.Entities.ArtisanProfile;
import com.eCommerce.Ecommerce.Entities.Seller;

@Repository
public interface ArtisanProfileRepository extends JpaRepository<ArtisanProfile, Long> {
    Optional<ArtisanProfile> findBySeller(Seller seller);
    Optional<ArtisanProfile> findBySellerId(Long sellerId);
}
