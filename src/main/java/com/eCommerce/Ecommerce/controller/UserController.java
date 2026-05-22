package com.eCommerce.Ecommerce.controller;

/**
 * Base URL: http://localhost:8080/user
 * This controller handles user-related web pages and operations
 */

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eCommerce.Ecommerce.Entities.Cart;
import com.eCommerce.Ecommerce.Entities.Orders;
import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Entities.Review;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Entities.User;
import com.eCommerce.Ecommerce.Repo.ProductRepo;
import com.eCommerce.Ecommerce.Services.CartService;

import com.eCommerce.Ecommerce.Services.OrderService;
import com.eCommerce.Ecommerce.Services.ProductService;
import com.eCommerce.Ecommerce.Services.ReviewService;
import com.eCommerce.Ecommerce.Services.SMSservice;
import com.eCommerce.Ecommerce.Services.SellerService;
import com.eCommerce.Ecommerce.Services.UserServices;
import com.eCommerce.Ecommerce.Services.UserSettingsService;

import jakarta.mail.MessagingException;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ProductService productService;

    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private com.eCommerce.Ecommerce.Repo.UserRepo userRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserServices userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private SMSservice sms;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private com.eCommerce.Ecommerce.Services.TipService tipService;

    @Autowired
    private com.eCommerce.Ecommerce.Services.ArtisanProfileService artisanProfileService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            // For regular login
            email = auth.getName();
        }
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "redirect:/login";
        }
        int userProductCount = userService.getProductCount();
        int userspendings = userService.getTotalSpendings();
        List<Orders> userOrders = orderService.getAllOrders(user.getId());

       
        model.addAttribute("orders", userOrders);
        model.addAttribute("userTotalSpendings", userspendings);
        model.addAttribute("userProductCount", userProductCount);
        return "user/userdashboard";
    }

    @GetMapping("/fix-images")
    @org.springframework.web.bind.annotation.ResponseBody
    public String fixImages() {
        List<Product> products = productRepo.findAll();
        for (Product product : products) {
            String title = product.getName();
            if ("Unique Beaded Necklace".equalsIgnoreCase(title)) {
                product.setImages("/images/beaded_necklace.png");
            } else if ("Unique Wood Carving".equalsIgnoreCase(title)) {
                product.setImages("/images/wood_carving.png");
            } else if ("Unique Warli Painting".equalsIgnoreCase(title)) {
                product.setImages("/images/warli_painting.png");
            } else if ("Indigenous Toda Embroidery".equalsIgnoreCase(title)) {
                product.setImages("/images/toda_embroidery.png");
            }
            productRepo.save(product);
        }
        return "Images updated successfully!";
    }



    @GetMapping("/base")
    public String base(Model model) {
        return "base";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contactus";
    }

    @PostMapping("/contact/submit")
    public String submitContactForm(@RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("message") String message,
            RedirectAttributes redirectAttributes) throws MessagingException  {
        sms.receviedquery(name, email, message);
       // base.html listens for 'message' (success) or 'alert*' keys
   
       // Optionally set the more detailed alert API used in base.html
       redirectAttributes.addFlashAttribute("alertTitle", "Success");
       redirectAttributes.addFlashAttribute("alertMessage", "Your message has been sent successfully!");
       redirectAttributes.addFlashAttribute("alertType", "success");
       return "redirect:/user/contact";
    }

    @GetMapping("/products")
    public String viewProducts1(Model model, @RequestParam(value = "category", required = false) String category) {
        List<Product> products;
        if (category != null && !category.isEmpty()) {
            products = productService.getProductsByCategory(category);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        return "user/products";
    }

    @GetMapping("/category/{slug}")
    public String viewCategory(@PathVariable String slug, Model model) {
        List<Product> products = productService.getProductsByCategory(slug);
        model.addAttribute("products", products);
        model.addAttribute("categorySlug", slug);
        // Map slug → display name & emoji
        java.util.Map<String, String[]> meta = new java.util.LinkedHashMap<>();
        meta.put("handicrafts",  new String[]{"🏺", "Tribal Handicrafts",          "Authentic handcrafted objects made by tribal artisans across India."});
        meta.put("paintings",    new String[]{"🖼️", "Gond & Tribal Paintings",     "Vibrant folk paintings — Gond, Warli, Madhubani and more."});
        meta.put("bamboo",       new String[]{"🎋", "Bamboo & Cane Crafts",        "Eco-friendly bamboo furniture, baskets and decorative items."});
        meta.put("jewelry",      new String[]{"💎", "Handmade Jewelry",             "Tribal silver, beaded and terracotta jewelry crafted by hand."});
        meta.put("textiles",     new String[]{"🧶", "Tribal Textiles & Fabrics",   "Handwoven fabrics and garments from tribal communities."});
        meta.put("wood",         new String[]{"🪵", "Wooden Tribal Art",           "Intricately carved wooden artifacts and sculptures."});
        meta.put("forest",       new String[]{"🌿", "Organic Forest Products",     "Pure forest-sourced produce — seeds, leaves, bark extracts."});
        meta.put("honey",        new String[]{"🍯", "Natural Honey & Herbs",       "Wild-harvested honey and herbal forest products."});
        meta.put("clay",         new String[]{"🏺", "Clay & Terracotta Art",       "Traditional clay pottery and terracotta decoratives."});
        meta.put("decor",        new String[]{"🎭", "Cultural Home Decor",         "Tribal art pieces that bring culture into your home."});
        meta.put("baskets",      new String[]{"🧺", "Handwoven Baskets",           "Hand-woven storage and decorative baskets in various styles."});
        meta.put("eco",          new String[]{"♻️", "Eco-Friendly Handmade",      "Sustainable, zero-waste products crafted with natural materials."});
        String[] info = meta.getOrDefault(slug, new String[]{"🌿", slug, "Browse products in this category."});
        model.addAttribute("categoryEmoji",       info[0]);
        model.addAttribute("categoryName",        info[1]);
        model.addAttribute("categoryDescription", info[2]);
        return "user/category";
    }


    @GetMapping("/details/{id}")
    public String viewProductDetail(@PathVariable("id") Long id, Model model) {
        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                model.addAttribute("error", "Product not found");
                return "user/productDetail";
            }
            
            // Get reviews for the product
            List<Review> reviews = reviewService.getReviewsByProductId(id);
            model.addAttribute("product", product);
            model.addAttribute("reviews", reviews);

            // Get related products
            List<Product> relatedProducts = productService.getRelatedProducts(product);
            model.addAttribute("relatedProducts", relatedProducts);

            // Artisan tipping analytics & Profile
            if (product.getSeller() != null) {
                model.addAttribute("artisanTotalSupport", tipService.getTotalTipsForArtisan(product.getSeller().getId()));
                model.addAttribute("artisanSupporterCount", tipService.getUniqueSupportersForArtisan(product.getSeller().getId()));
                
                java.util.Optional<com.eCommerce.Ecommerce.Entities.ArtisanProfile> artisanProfile = artisanProfileService.getProfileBySeller(product.getSeller());
                if (artisanProfile.isPresent()) {
                    model.addAttribute("artisanProfile", artisanProfile.get());
                }
            } else {
                model.addAttribute("artisanTotalSupport", 0);
                model.addAttribute("artisanSupporterCount", 0);
            }
            return "user/productDetail";
        } catch (Exception e) {
            model.addAttribute("error", "Error retrieving product details: " + e.getMessage());
            return "user/productDetail";
        }
    }

    @GetMapping("/main")
    public String viewProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products available nahi mila.");
        }
        model.addAttribute("products", products);

        return "user/main"; // user/main.html
    }

    @GetMapping("/products/view/{productId}")
    public String viewProductDetails(@PathVariable Long productId, Model model) {
        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                model.addAttribute("error", "Product not found");
                return "user/productDetail";
            }
            // Get reviews for the product
            List<Review> reviews = reviewService.getReviewsByProductId(productId);

            model.addAttribute("product", product);
            model.addAttribute("reviews", reviews);

            // Get related products
            List<Product> relatedProducts = productService.getRelatedProducts(product);
            model.addAttribute("relatedProducts", relatedProducts);

            // Artisan tipping analytics & Profile
            if (product.getSeller() != null) {
                model.addAttribute("artisanTotalSupport", tipService.getTotalTipsForArtisan(product.getSeller().getId()));
                model.addAttribute("artisanSupporterCount", tipService.getUniqueSupportersForArtisan(product.getSeller().getId()));
                
                java.util.Optional<com.eCommerce.Ecommerce.Entities.ArtisanProfile> artisanProfile = artisanProfileService.getProfileBySeller(product.getSeller());
                if (artisanProfile.isPresent()) {
                    model.addAttribute("artisanProfile", artisanProfile.get());
                }
            } else {
                model.addAttribute("artisanTotalSupport", 0);
                model.addAttribute("artisanSupporterCount", 0);
            }

            return "user/productDetail";
        } catch (Exception e) {
            model.addAttribute("error", "Error retrieving product details: " + e.getMessage());
            return "user/productDetail";
        }
    }

    @GetMapping("/orders")
    public String orders() {
        return "user/orderHistory";
    }

    @GetMapping("/checkout")
    public String checkout(
            Model model,
            jakarta.servlet.http.HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            // For regular login
            email = auth.getName();
        }
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "redirect:/login";
        }

        Cart cart = cartService.getCartByUser(user);

        // Guard: prevent direct URL access — cart must have items
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return "redirect:/user/cart";
        }

        // Guard: must come from the cart page (Referer check)
        String referer = request.getHeader("Referer");
        if (referer == null || (!referer.contains("/user/cart") && !referer.contains("/user/checkout"))) {
            return "redirect:/user/cart";
        }

        model.addAttribute("cart", cart);
        model.addAttribute("razorpayKey", razorpayKey);
        return "user/checkout";
    }

    @GetMapping("/history")
    public String orderHistory() {
        return "user/history";
    }

    @GetMapping("/order/invoice/{id}")
    public String viewOrderInvoice(@PathVariable Long id, Model model) {
        Orders order = orderService.findOrderById(id);
        if (order == null) {
            return "redirect:/user/dashboard";
        }
        model.addAttribute("order", order);
        return "seller/invoice"; // reuse the same invoice template
    }

    @GetMapping("/payment")
    public String payment() {
        return "user/payment";
    }

    @GetMapping("/ordersummary")
    public String ordersummary(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            // For regular login
            email = auth.getName();
        }
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "redirect:/login";
        }
        List<Orders> userOrders = orderService.getAllOrders(user.getId());
       
        model.addAttribute("orders", userOrders);
        return "user/ordersummary";
    }

    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        System.out.println("Fetching delivered orders for order ID: " + id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            // For regular login
            email = auth.getName();
        }
        System.out.println("Logged in user email: " + email);
        User user = userRepository.findByEmail(email);
        if (user == null)
            return "redirect:/login";

      
        Orders deliverOrder = orderService.findOrderById(id);
        Seller seller = sellerService.getSellerById(deliverOrder.getSellerId());

 
        model.addAttribute("deliverOrder", deliverOrder);
        model.addAttribute("seller", seller);
        return "user/userDeliveredOrders";
    }

    @GetMapping("/orders/cancel/{id}")
    public String cancelOrder(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        

       boolean status = orderService.getOrdersById(id);
       if(status){
              redirectAttributes.addFlashAttribute("alertType", "success");
                redirectAttributes.addFlashAttribute("alertTitle", "Order Cancellation Successful");
                redirectAttributes.addFlashAttribute("alertMessage", "Your order has been successfully cancelled.");
       }else{
              redirectAttributes.addFlashAttribute("alertType", "error");
                redirectAttributes.addFlashAttribute("alertTitle", "Order Cancellation Failed");        
                redirectAttributes.addFlashAttribute("alertMessage", "Unable to cancel the order. Please contact customer support.");
       }
       return "redirect:/user/ordersummary";

    }
}