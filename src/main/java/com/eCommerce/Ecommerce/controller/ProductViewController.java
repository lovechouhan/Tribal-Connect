package com.eCommerce.Ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.eCommerce.Ecommerce.Entities.Product;
import com.eCommerce.Ecommerce.Services.ProductService;



@Controller
@RequestMapping("/products")
public class ProductViewController {

    @Autowired
    private ProductService productService;

    @GetMapping("/women")
    public String women(Model model) {
        List<Product> products = productService.getProductsByCategory("women");
        model.addAttribute("products", products);
        return "user/womenproduct";
    }

    @GetMapping("/kids")
    public String kids(Model model) {
        List<Product> products = productService.getProductsByCategory("kids");
        model.addAttribute("products", products);
        return "user/kidz";
    }

    @GetMapping("/men")
    public String men(Model model) {
        List<Product> products = productService.getProductsByCategory("men");
        model.addAttribute("products", products);
        return "user/menproduct";
    }

    @GetMapping("/electronics")
    public String electronics(Model model) {
        List<Product> products = productService.getProductsByCategory("electronics");
        model.addAttribute("products", products);
        return "user/electronics";
    }

    @GetMapping("/accessories")
    public String accessories(Model model) {
        List<Product> products = productService.getProductsByCategory("accessories");
        model.addAttribute("products", products);
        return "user/accessories";
    }

    @GetMapping("/footwear")
    public String footwear(Model model) {
        List<Product> products = productService.getProductsByCategory("footwear");
        model.addAttribute("products", products);
        return "user/footwear";
    }

    @GetMapping("/bags")
    public String bags(Model model) {
        List<Product> products = productService.getProductsByCategory("bags");
        model.addAttribute("products", products);
        return "user/bags";
    }

    @GetMapping("/audio")
    public String audio(Model model) {
        List<Product> products = productService.getProductsByCategory("audio");
        model.addAttribute("products", products);
        return "user/audio";
    }
}