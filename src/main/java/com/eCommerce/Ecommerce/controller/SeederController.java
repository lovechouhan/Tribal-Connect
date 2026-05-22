package com.eCommerce.Ecommerce.controller;

import com.eCommerce.Ecommerce.Entities.ArtisanProfile;
import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Repo.ProductRepo;
import com.eCommerce.Ecommerce.Repo.SellerRepo;
import com.eCommerce.Ecommerce.Repository.ArtisanProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
public class SeederController {

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private SellerRepo sellerRepository;

    @Autowired
    private ArtisanProfileRepository artisanProfileRepository;

    @GetMapping("/api/seed/tribal")
    public String seedTribalArt() {
        // Define the 5 core tribal sellers/origins
        String[][] sellerConfigs = {
            {"Tribal Arts Co.", "tribal_art_seller@tribalconnect.com", "Kavita Devi & Group", "Mixed Tribal Cooperative", "Jharkhand & Odisha", "Traditional Handicrafts & Organic Produce", "15", "A collective of master artisans from various indigenous communities dedicated to preserving ancient crafting techniques.", "Every piece is sustainably sourced from local forests and handcrafted using generations-old tribal methods without artificial dyes or machinery.", "https://placehold.co/400x400/6B4226/FDF6E9?text=Tribal+Arts+Co."},
            {"Bastar Art", "bastar_art@tribalconnect.com", "Rameshwar Baghel", "Bastar Dhokra & Wrought Iron Artisans", "Kondagaon, Bastar, Chhattisgarh", "Dhokra (Lost Wax Metal Casting) & Iron Craft", "22", "Master artisan specializing in Bastar Dhokra art, creating exquisite bell metal figurines depicting tribal folklore.", "Using the ancient lost-wax technique, beeswax is sculpted, encased in clay, and molten metal is poured to create unique, seamless metal artifacts.", "https://placehold.co/400x400/8B5E3C/FDF6E9?text=Bastar+Art"},
            {"Gond Tribe", "gond_tribe@tribalconnect.com", "Bhajju Shyam & Family", "Gond Indigenous Community", "Patangarh, Madhya Pradesh", "Gond Folk Paintings & Wall Art", "25", "Renowned Gond artist family renowned for bringing the mystical nature-inspired folklore of the Gond tribe onto canvas.", "Intricate lines and dots are meticulously painted using natural mineral pigments to breathe life into sacred forest spirits and vibrant wildlife.", "https://placehold.co/400x400/A0522D/FDF6E9?text=Gond+Tribe"},
            {"Bhil Artisans", "bhil_artisans@tribalconnect.com", "Shanta Bhuriya", "Bhil Tribe", "Jhabua, Madhya Pradesh", "Pithora Art & Beadwork", "18", "Pioneering Bhil artisan crafting traditional beadwork jewelry and vibrant Pithora ritual paintings.", "Each piece reflects the rich cultural tapestry of the Bhil community, using vibrant beads and organic colors symbolizing joy and harvest.", "https://placehold.co/400x400/CD853F/FDF6E9?text=Bhil+Artisans"},
            {"Warli Co-op", "warli_coop@tribalconnect.com", "Jivya Soma Mashe Legacy Group", "Warli Tribe", "Palghar, Maharashtra", "Warli Paintings & Terracotta Decor", "30", "A dedicated cooperative practicing the pristine, rhythmic Warli art form depicting harmony between humans and nature.", "Created using a mixture of rice paste and water with gum on a mud and cow-dung treated surface, celebrating communal solidarity and folklore.", "https://placehold.co/400x400/D2691E/FDF6E9?text=Warli+Co-op"}
        };

        List<Seller> sellers = new ArrayList<>();

        for (String[] config : sellerConfigs) {
            String businessName = config[0];
            String email = config[1];
            String artisanName = config[2];
            String tribe = config[3];
            String region = config[4];
            String specialty = config[5];
            int exp = Integer.parseInt(config[6]);
            String bio = config[7];
            String process = config[8];
            String img = config[9];

            Seller seller = sellerRepository.findByEmail(email);
            if (seller == null) {
                seller = new Seller();
                seller.setSellerName(businessName);
                seller.setEmail(email);
                seller.setPassword("password");
                seller.setPhoneNumber("9999999999");
                seller.getBussinessDetails().setBusinessName(businessName);
                seller.getPickupAddress().setCity(region.contains(",") ? region.substring(0, region.indexOf(",")) : region);
                seller.getPickupAddress().setState(region.contains(",") ? region.substring(region.lastIndexOf(",") + 1).trim() : region);
                seller.setRole("SELLER");
                seller = sellerRepository.save(seller);
            }
            sellers.add(seller);

            // Create ArtisanProfile if not exists
            Optional<ArtisanProfile> optProfile = artisanProfileRepository.findBySeller(seller);
            if (optProfile.isEmpty()) {
                ArtisanProfile profile = new ArtisanProfile();
                profile.setSeller(seller);
                profile.setArtisanName(artisanName);
                profile.setTribeOrCommunity(tribe);
                profile.setRegionOrState(region);
                profile.setCraftSpecialty(specialty);
                profile.setYearsOfExperience(exp);
                profile.setShortBio(bio);
                profile.setHandcraftedProcess(process);
                profile.setArtisanImage(img);
                artisanProfileRepository.save(profile);
            }
        }

        String[] adjs = { "Authentic", "Traditional", "Handcrafted", "Vintage", "Cultural", "Indigenous", "Rustic", "Exquisite", "Sacred", "Tribal" };
        String[] colors = { "Brown", "Earthy Red", "Monochrome", "Multicolor", "Ochre", "Terracotta", "Natural Wood", "Forest Green" };
        
        String[] categories = {
            "handicrafts", "paintings", "bamboo", "jewelry", "textiles", 
            "wood", "forest", "honey", "clay", "decor", "baskets", "eco"
        };
        
        Random random = new Random();
        List<Product> products = new ArrayList<>();

        int productCounter = 0;

        for (int catIdx = 0; catIdx < categories.length; catIdx++) {
            String cat = categories[catIdx];
            for (int i = 1; i <= 10; i++) {
                productCounter++;
                Product product = new Product();
                
                String name = adjs[random.nextInt(adjs.length)] + " " + cat.substring(0, 1).toUpperCase() + cat.substring(1) + " Item " + i;
                product.setName(name);
                product.setDescription("A beautiful and authentic piece of " + cat + ". Perfect for home decor or gifting. Handcrafted with love by indigenous artisans.");

                // Systematically distribute selling prices across the 4 filter brackets
                int sellingPrice;
                if (i <= 2) {
                    sellingPrice = 250 + random.nextInt(200); // Under 500 (250-450)
                } else if (i <= 5) {
                    sellingPrice = 550 + random.nextInt(850); // 500 to 1500 (550-1400)
                } else if (i <= 8) {
                    sellingPrice = 1600 + random.nextInt(3200); // 1500 to 5000 (1600-4800)
                } else {
                    sellingPrice = 5200 + random.nextInt(3500); // Above 5000 (5200-8700)
                }

                int mrp = sellingPrice + 100 + random.nextInt(1000);
                product.setMRPprice(mrp);
                product.setSellingPrice(sellingPrice);
                product.setDiscountPrice(product.getMRPprice() - product.getSellingPrice());

                // Systematically assign sellers so every category has diverse artisan origins
                Seller seller = sellers.get(productCounter % sellers.size());
                product.setSellerName(seller.getSellerName());
                product.setSeller(seller);
                
                product.setQuantity(1 + random.nextInt(20));
                product.setColor(colors[random.nextInt(colors.length)]);
                product.setBrand("Tribal Heritage");
                
                // Using placehold.co with category name
                product.setImages("https://placehold.co/400x400/8B5E3C/FDF6E9?text=" + cat);
                
                product.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
                product.setCategory(cat);
                product.setNumRatings(0);
                product.setRatings(new ArrayList<>());
                product.setReviews(new ArrayList<>());
                product.setSizes(new ArrayList<>());

                products.add(product);
            }
        }

        productRepository.saveAll(products);

        return "Successfully seeded 120 products across 12 tribal categories with 5 distinct artisan origins and balanced price brackets!";
    }
}

