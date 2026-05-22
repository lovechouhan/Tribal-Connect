// package com.eCommerce.Ecommerce.controller;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.UUID;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

// import com.eCommerce.Ecommerce.Entities.Product;
// import com.eCommerce.Ecommerce.Entities.Seller;
// import com.eCommerce.Ecommerce.Form.createproduct;
// import com.eCommerce.Ecommerce.Services.DriveUploadService;
// import com.eCommerce.Ecommerce.Services.ProductService;
// import com.eCommerce.Ecommerce.Services.SellerService;

// @Controller
// @RequestMapping("/sellers")
// public class SellerProductController {

//     @Autowired
//     private ProductService productService;

//     @Autowired
//     private SellerService sellerService;

//     @Autowired
//     private DriveUploadService imageService;//

//     @Autowired
//     private com.eCommerce.Ecommerce.Repo.ProductRepo productRepo;

    

//     @Autowired
//     private com.eCommerce.Ecommerce.Repo.SellerRepo sellerRepository;

//     @GetMapping("/{sellerId}")
//     public ResponseEntity<List<Product>> getProductBySellerId(@PathVariable Long sellerId) {
//         List<Product> products = productService.getProductsBySellerId(sellerId);
//         return ResponseEntity.ok(products);
//     }

//     @GetMapping("/products")
//     public String getAllProducts(Model model) {
//         model.addAttribute("products", new createproduct());
//         return "seller/createproduct";
//     }

//     @GetMapping("/getProducts")
//     public String getSellerProducts(Model model) {
//         // Get the currently logged in user
//         org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
//                 .getContext().getAuthentication();

//         // Since our seller implements UserDetails, we can cast directly
//         // The email is prefixed with 'seller_' in the loadUserByUsername method
//         String email = authentication.getName().replace("seller_", "");

//         try {
//             // Get seller by email
//             Seller seller = sellerService.getSellerByEmail(email);
//             if (seller == null) {
//                 model.addAttribute("error", "Seller account not found");
//                 return "seller/sellerProducts";
//             }

//             // Get products for this seller
//             List<Product> products = productService.getProductsBySellerId(seller.getId());

//             // Add products to the model
//             model.addAttribute("products", products);
//             model.addAttribute("sellerName", seller.getSellerName());

//             return "seller/sellerProducts";
//         } catch (Exception e) {
//             model.addAttribute("error", "Error retrieving products: " + e.getMessage());
//             return "seller/sellerProducts";
//         }
//     }

//     // DELETE endpoint for API/AJAX calls
//     @DeleteMapping("/products/{id}")
//     public ResponseEntity<?> deleteProductApi(@PathVariable Long id) {
//         try {
//             // Get current seller
//             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//             String email = authentication.getName().replace("seller_", "");
//             Seller seller = sellerService.getSellerByEmail(email);

//             // Get product
//             Product product = productService.getProductById(id);

//             // Check if product belongs to seller
//             if (!product.getSeller().getId().equals(seller.getId())) {
//                 return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                         .body("You don't have permission to delete this product");
//             }

//             productService.deleteProduct(id);
//             return ResponseEntity.ok().build();
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .body("Error deleting product: " + e.getMessage());
//         }
//     }

//     // POST endpoint for form submissions
//     @PostMapping("/products/delete/{id}")
//     public String deleteProduct(@PathVariable Long id, Model model) {
//         try {
//             // Get current seller
//             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//             String email = authentication.getName().replace("seller_", "");
//             Seller seller = sellerService.getSellerByEmail(email);

//             // Get product
//             Product product = productService.getProductById(id);

//             // Check if product belongs to seller
//             if (!product.getSeller().getId().equals(seller.getId())) {
//                 model.addAttribute("error", "You don't have permission to delete this product");
//                 return "redirect:/sellers/getProducts";
//             }

//             productService.deleteProduct(id);
//             model.addAttribute("success", "Product deleted successfully");
//         } catch (Exception e) {
//             model.addAttribute("error", "Error deleting product: " + e.getMessage());
//         }
//         return "redirect:/sellers/getProducts";
//     }


//     @GetMapping("/products/view/{productId}")
//     public String viewProductDetails(@PathVariable Long productId, Model model) {
//         try {
//             Product product = productService.getProductById(productId);
//             if (product == null) {
//                 model.addAttribute("error", "Product not found");
//                 return "seller/sellerproductdetails";
//             }
//             model.addAttribute("product", product);
//             return "seller/sellerproductdetails";
//         } catch (Exception e) {
//             model.addAttribute("error", "Error retrieving product details: " + e.getMessage());
//             return "seller/sellerproductdetails";
//         }
//     }

//     @PostMapping("/products/add")
//     public String createProduct(@ModelAttribute("products") createproduct products, Authentication authentication) {
//         try {
//             // Get the currently logged in seller
//             authentication = SecurityContextHolder.getContext().getAuthentication();
//             System.out.println("user ko naam: " + authentication.getName());
//             String email = authentication.getName().replace("seller_", "");
//             Seller seller = sellerService.getSellerByEmail(email);

//             if (seller == null) {
//                 throw new RuntimeException("Seller not found");
//             }

//             String filename = UUID.randomUUID().toString();
//             String fileURL;

//             if (products.getImages() == null || products.getImages().isEmpty()) {
//                 fileURL = ""; // Default image URL
//             } else {
//                fileURL = imageService.uploadFile(products.getImages());
// // Assuming .jpg, adjust as needed

//             }

       

        

//             Product product = new Product();
//             product.setName(products.getName());
//             product.setDescription(products.getDescription());
//             product.setMRPprice(products.getMRPprice());
//             product.setSellingPrice(products.getSellingPrice());
//             product.setQuantity(products.getQuantity());
//             product.setColor(products.getColor());
//             product.setBrand(products.getBrand());
//             product.setCategory(products.getCategory());
             

//             product.setDiscountPrice(products.getDiscountPrice());
//             product.setCreatedAt(LocalDateTime.now());
//             product.setSizes(List.of("S", "M", "L", "XL")); // Default sizes, can be modified as needed
//             product.setSeller(seller); // Set the seller for the product

//             product.setImages(fileURL); // Set the image URL

//             productRepo.save(product);
           
//             return "redirect:/sellers/getProducts";
//         } catch (Exception e) {
//             // Log the error and return to the form with error message
//             return "redirect:/sellers/products?error=" + e.getMessage();
//         }
//     }

//     @DeleteMapping("/products/delete/{productId}")
//     public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
//         try {
//             productService.deleteProduct(productId);
//             return new ResponseEntity<>(HttpStatus.OK);
//         } catch (Exception e) {
//             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//         }
//     }

//     @GetMapping("/products/edit/{productId}")
//     public String editProductItem(@PathVariable Long productId, Model model) {
//         try {
//             Product product = productService.getProductById(productId);
//             System.out.println("Editing hone wala product: " + product);
//             if (product == null) {
//                 model.addAttribute("error", "Product not found");
//                 return "seller/updateproduct";
//             }
//             model.addAttribute("product", product); // Add product to the model instead of deleting it

//             return "seller/updateproduct";
//         } catch (Exception e) {
//             model.addAttribute("error", "Error retrieving product: " + e.getMessage());
//             return "seller/updateproduct";
//         }
//     }

//     @PostMapping("/products/update/{productId}")
//     public String updateProduct(@PathVariable Long productId,
//             @ModelAttribute("product") createproduct updatedProduct,
//             Model model) {
//         try {
//             // Get current seller
//             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//             String email = authentication.getName().replace("seller_", "");
//             Seller seller = sellerService.getSellerByEmail(email);

//             // Get existing product
//             Product existingProduct = productService.getProductById(productId);

//             // Check if product belongs to seller
//             if (!existingProduct.getSeller().getId().equals(seller.getId())) {
//                 model.addAttribute("error", "You don't have permission to update this product");
//                 return "redirect:/sellers/getProducts";
//             }

//             // Handle image upload
//             String fileURL = existingProduct.getImages(); // Keep existing image by default
//             if (updatedProduct.getImages() != null && !updatedProduct.getImages().isEmpty()) {
//                 String filename = UUID.randomUUID().toString();
                
//                 fileURL = imageService.uploadFile(updatedProduct.getImages());
//             }

//             // Update product details
//             existingProduct.setName(updatedProduct.getName());
//             existingProduct.setDescription(updatedProduct.getDescription());
//             existingProduct.setMRPprice(updatedProduct.getMRPprice());
//             existingProduct.setSellingPrice(updatedProduct.getSellingPrice());
//             existingProduct.setQuantity(updatedProduct.getQuantity());
//             existingProduct.setColor(updatedProduct.getColor());
//             existingProduct.setBrand(updatedProduct.getBrand());
//             existingProduct.setDiscountPrice(updatedProduct.getDiscountPrice());
//             existingProduct.setImages(fileURL);

//             // Save updated product
//             productRepo.save(existingProduct);

//             model.addAttribute("success", "Product updated successfully");
//             return "redirect:/sellers/getProducts";
//         } catch (Exception e) {
//             model.addAttribute("error", "Error updating product: " + e.getMessage());
//             return "redirect:/sellers/products/edit/" + productId;
//         }
//     }
// }


package com.eCommerce.Ecommerce.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Entities.Seller;
import com.eCommerce.Ecommerce.Form.createproduct;
import com.eCommerce.Ecommerce.Services.CloudinaryImageService;
import com.eCommerce.Ecommerce.Services.ProductService;
import com.eCommerce.Ecommerce.Services.SellerService;

@Controller
@RequestMapping("/sellers")
public class SellerProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private CloudinaryImageService imageService; 

    @Autowired
    private com.eCommerce.Ecommerce.Repo.ProductRepo productRepo;

    @Autowired
    private com.eCommerce.Ecommerce.Repo.SellerRepo sellerRepository;

    // ✅ Get all products by sellerId (API)
    @GetMapping("/api/{sellerId}")
    public ResponseEntity<List<Product>> getProductBySellerId(@PathVariable Long sellerId) {
        List<Product> products = productService.getProductsBySellerId(sellerId);
        return ResponseEntity.ok(products);
    }

    // ✅ Show add product page
    @GetMapping({"/products", "/addProduct"})
    public String getAllProducts(Model model) {
        model.addAttribute("products", new createproduct());
        return "seller/createproduct";
    }

    // ✅ Seller’s own products
    @GetMapping("/getProducts")
    public String getSellerProducts(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName().replace("seller_", "");

        try {
            Seller seller = sellerService.getSellerByEmail(email);
            if (seller == null) {
                model.addAttribute("error", "Seller account not found");
                return "seller/sellerProducts";
            }

            List<Product> products = productService.getProductsBySellerId(seller.getId());
            model.addAttribute("products", products);
            model.addAttribute("sellerName", seller.getSellerName());
            return "seller/sellerProducts";
        } catch (Exception e) {
            model.addAttribute("error", "Error retrieving products: " + e.getMessage());
            return "seller/sellerProducts";
        }
    }

    // ✅ Add new product (with image upload to Google Drive)
    @PostMapping("/products/add")
    public String createProduct(@ModelAttribute("products") createproduct products, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName().replace("seller_", "");
            Seller seller = sellerService.getSellerByEmail(email);

            if (seller == null) throw new RuntimeException("Seller not found");

            Map data = this.imageService.upload(products.getImages());
            String fileURL = data.get("url").toString();
            Product product = new Product();
            product.setName(products.getName());
            product.setDescription(products.getDescription());
            product.setMRPprice(products.getMRPprice());
            product.setSellingPrice(products.getSellingPrice());
            product.setQuantity(products.getQuantity());
            product.setColor(products.getColor());
            product.setBrand(products.getBrand());
            product.setCategory(products.getCategory());
            product.setDiscountPrice(products.getDiscountPrice());
            product.setCreatedAt(LocalDateTime.now());
            product.setSizes(List.of("S", "M", "L", "XL"));
            product.setSeller(seller);
            product.setImages(fileURL);
            product.setRatings(List.of());
            product.setReviews(List.of());
            productRepo.save(product);
            redirectAttributes.addFlashAttribute("alertType", "success");
            redirectAttributes.addFlashAttribute("alertTitle", "Success!");
            redirectAttributes.addFlashAttribute("alertMessage", "Product created successfully.");

            return "redirect:/sellers/getProducts";
        } catch (Exception e) {
            e.printStackTrace();
             redirectAttributes.addFlashAttribute("alertType", "error");
        redirectAttributes.addFlashAttribute("alertTitle", "Failed!");
        redirectAttributes.addFlashAttribute("alertMessage", "Could not add product to cart.");
            return "redirect:/sellers/products?error=" + e.getMessage();
        }
    }

    // ✅ Edit product (update info + optionally upload new image)
    @PostMapping("/products/update/{productId}")
    public String updateProduct(@PathVariable Long productId,
                                @ModelAttribute("product") createproduct updatedProduct,
                                Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName().replace("seller_", "");
            Seller seller = sellerService.getSellerByEmail(email);

            Product existingProduct = productService.getProductById(productId);

            if (!existingProduct.getSeller().getId().equals(seller.getId())) {
                model.addAttribute("error", "You don't have permission to update this product");
                return "redirect:/sellers/getProducts";
            }
          
             Map data = this.imageService.upload(updatedProduct.getImages());
            String fileURL = data.get("url").toString();

            existingProduct.setName(updatedProduct.getName());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setMRPprice(updatedProduct.getMRPprice());
            existingProduct.setSellingPrice(updatedProduct.getSellingPrice());
            existingProduct.setQuantity(updatedProduct.getQuantity());
            existingProduct.setColor(updatedProduct.getColor());
            existingProduct.setBrand(updatedProduct.getBrand());
            existingProduct.setDiscountPrice(updatedProduct.getDiscountPrice());
            existingProduct.setImages(fileURL);

            productRepo.save(existingProduct);
            model.addAttribute("success", "Product updated successfully");
            return "redirect:/sellers/getProducts";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error updating product: " + e.getMessage());
            return "redirect:/sellers/products/edit/" + productId;
        }
    }

    // ✅ Delete product
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName().replace("seller_", "");
            Seller seller = sellerService.getSellerByEmail(email);

            Product product = productService.getProductById(id);

            if (!product.getSeller().getId().equals(seller.getId())) {
                model.addAttribute("error", "You don't have permission to delete this product");
                return "redirect:/sellers/getProducts";
            }

            productService.deleteProduct(id);
            model.addAttribute("success", "Product deleted successfully");
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/sellers/getProducts";
    }

    // ✅ View single product details
    @GetMapping("/products/view/{productId}")
    public String viewProductDetails(@PathVariable Long productId, Model model) {
        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                model.addAttribute("error", "Product not found");
                return "seller/sellerproductdetails";
            }
            model.addAttribute("product", product);
            return "seller/sellerproductdetails";
        } catch (Exception e) {
            model.addAttribute("error", "Error retrieving product details: " + e.getMessage());
            return "seller/sellerproductdetails";
        }
    }

    // ✅ Edit product page
    @GetMapping("/products/edit/{productId}")
    public String editProductItem(@PathVariable Long productId, Model model) {
        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                model.addAttribute("error", "Product not found");
                return "seller/updateproduct";
            }
            model.addAttribute("product", product);
            return "seller/updateproduct";
        } catch (Exception e) {
            model.addAttribute("error", "Error retrieving product: " + e.getMessage());
            return "seller/updateproduct";
        }
    }
}
