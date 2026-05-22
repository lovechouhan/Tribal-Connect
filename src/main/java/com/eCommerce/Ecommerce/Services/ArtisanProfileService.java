package com.eCommerce.Ecommerce.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eCommerce.Ecommerce.Entities.ArtisanProfile;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Repository.ArtisanProfileRepository;

@Service
public class ArtisanProfileService {

    @Autowired
    private ArtisanProfileRepository artisanProfileRepository;

    public Optional<ArtisanProfile> getProfileBySeller(Seller seller) {
        return artisanProfileRepository.findBySeller(seller);
    }

    public ArtisanProfile saveOrUpdateProfile(ArtisanProfile profile) {
        return artisanProfileRepository.save(profile);
    }
}
