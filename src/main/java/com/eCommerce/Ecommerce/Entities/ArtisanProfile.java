package com.eCommerce.Ecommerce.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "artisan_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "seller")
public class ArtisanProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "seller_id", unique = true)
    private Seller seller;

    private String artisanName;
    private String tribeOrCommunity;
    private String regionOrState;
    private String craftSpecialty;
    private Integer yearsOfExperience;

    @Column(length = 500)
    private String shortBio;

    @Column(length = 2000)
    private String handcraftedProcess;

    private String artisanImage; // URL for the uploaded image

}
