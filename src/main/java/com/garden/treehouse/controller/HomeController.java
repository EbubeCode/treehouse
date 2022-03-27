package com.garden.treehouse.controller;

import com.garden.treehouse.model.*;
import com.garden.treehouse.repos.ProductRepository;
import com.garden.treehouse.services.*;
import com.garden.treehouse.utility.USConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Controller
public class HomeController {
    private final UserService userService;
    private final ProductService productService;
    private final UserPaymentService userPaymentService;
    private final UserShippingService userShippingService;
    private final ProductRepository productRepository;
    private final CartItemService cartItemService;
    private final OrderService orderService;

    public HomeController(UserService userService, ProductService productService,
                          UserPaymentService userPaymentService,
                          UserShippingService userShippingService,
                          ProductRepository productRepository, CartItemService cartItemService,
                          OrderService orderService) {
        this.userService = userService;
        this.productService = productService;
        this.userPaymentService = userPaymentService;
        this.userShippingService = userShippingService;
        this.productRepository = productRepository;
        this.cartItemService = cartItemService;
        this.orderService = orderService;
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

        List<Integer> qtyList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);


        return "productDetail";
    }

    @GetMapping("/productDetailS")
    public String productDetailS(
            @RequestParam("id") Long id, Model model, Principal principal
    ) {

        Product product = productService.findById(id);

        model.addAttribute("product", product);

        List<Integer> qtyList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);


        return "productDetail";
    }



}
