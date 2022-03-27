package com.garden.treehouse.controller;

import com.garden.treehouse.model.Product;
import com.garden.treehouse.model.User;
import com.garden.treehouse.repos.ProductRepository;
import com.garden.treehouse.services.ProductService;
import com.garden.treehouse.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;


@Controller
public class HomeController {
    private final UserService userService;
    private final ProductService productService;
    private final ProductRepository productRepository;

    public HomeController(UserService userService, ProductService productService,
                          ProductRepository productRepository) {
        this.userService = userService;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @RequestMapping("/")
    public String index(Model model) {
        var products = productRepository.findTop4ByOrderByIdDesc().orElse(List.of());
        model.addAttribute("products", products);

        var plants = productRepository.findTop4ByCategoryOrderByIdDesc("Plants").orElse(List.of());
        model.addAttribute("plants", plants);

        var fruits = productRepository.findTop4ByCategoryOrderByIdDesc("Fruits").orElse(List.of());
        model.addAttribute("fruits", fruits);

        var veges = productRepository.findTop4ByCategoryOrderByIdDesc("Vegetables").orElse(List.of());
        model.addAttribute("veges", veges);

        var tools = productRepository.findTop4ByCategoryOrderByIdDesc("Tools").orElse(List.of());
        model.addAttribute("tools", tools);

        var seeds = productRepository.findTop4ByCategoryOrderByIdDesc("Seeds").orElse(List.of());
        model.addAttribute("seeds", seeds);

        return "index";
    }


    @GetMapping("/productRack")
    public String bookshelf(Model model, Principal principal) {

        if (principal != null) {
            String username = principal.getName();
            User user = userService.findByEmail(username);
            model.addAttribute("user", user);
        }

        List<Product> products = productService.findAll();
        model.addAttribute("productList", products);

        return "productRack";
    }

    @GetMapping("/productDetail")
    public String productDetail(
            @RequestParam("id") Long id, Model model, Principal principal
    ) {
        if (principal != null) {
            String username = principal.getName();
            User user = userService.findByEmail(username);
            model.addAttribute("user", user);
        }

        Product product = productService.findById(id);

        model.addAttribute("product", product);



        return "productDetail";
    }

    @GetMapping("/productDetailS")
    public String productDetailS(
            @RequestParam("id") Long id, Model model) {

        Product product = productService.findById(id);

        model.addAttribute("product", product);

        return "productDetail";
    }



}
