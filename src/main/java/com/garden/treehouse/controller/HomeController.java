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


    @RequestMapping("/listOfCreditCards")
    public String listOfCreditCards(
            Model model, Principal principal, HttpServletRequest request
    ) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("orderList", user.getOrderList());

        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("classActiveBilling", true);
        model.addAttribute("listOfShippingAddresses", true);

        return "myProfile";
    }


    @RequestMapping("/addNewCreditCard")
    public String addNewCreditCard(
            Model model, Principal principal
    ) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);

        model.addAttribute("addNewCreditCard", true);
        model.addAttribute("classActiveBilling", true);
        model.addAttribute("listOfShippingAddresses", true);

        UserBilling userBilling = new UserBilling();
        UserPayment userPayment = new UserPayment();


        model.addAttribute("userBilling", userBilling);
        model.addAttribute("userPayment", userPayment);

        List<String> stateList = USConstants.listOfUSStatesCode;
        Collections.sort(stateList);
        model.addAttribute("stateList", stateList);
        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("orderList", user.getOrderList());

        return "myProfile";
    }


//    @RequestMapping(value = "/addNewCreditCard", method = RequestMethod.POST)
//    public String addNewCreditCard(
//            @ModelAttribute("userPayment") UserPayment userPayment,
//            @ModelAttribute("userBilling") UserBilling userBilling,
//            Principal principal, Model model
//    ) {
//        User user = userService.findByEmail(principal.getName());
//        userService.updateUserBilling(userBilling, userPayment, user);
//
//        model.addAttribute("user", user);
//        model.addAttribute("userPaymentList", user.getUserPaymentList());
//        model.addAttribute("userShippingList", user.getUserShippingList());
//        model.addAttribute("listOfCreditCards", true);
//        model.addAttribute("classActiveBilling", true);
//        model.addAttribute("listOfShippingAddresses", true);
//        model.addAttribute("orderList", user.getOrderList());
//
//        return "myProfile";
//    }



    @RequestMapping("/updateCreditCard")
    public String updateCreditCard(
            @ModelAttribute("id") Long creditCardId, Principal principal, Model model
    ) {
        User user = userService.findByEmail(principal.getName());
        UserPayment userPayment = userPaymentService.findById(creditCardId);

        if (user.getId() != userPayment.getUser().getId()) {
            return "badRequestPage";
        } else {
            model.addAttribute("user", user);
            UserBilling userBilling = userPayment.getUserBilling();
            model.addAttribute("userPayment", userPayment);
            model.addAttribute("userBilling", userBilling);

            List<String> stateList = USConstants.listOfUSStatesCode;
            Collections.sort(stateList);
            model.addAttribute("stateList", stateList);

            model.addAttribute("addNewCreditCard", true);
            model.addAttribute("classActiveBilling", true);
            model.addAttribute("listOfShippingAddresses", true);

            model.addAttribute("userPaymentList", user.getUserPaymentList());
            model.addAttribute("userShippingList", user.getUserShippingList());
            model.addAttribute("orderList", user.getOrderList());

            return "myProfile";
        }
    }

    @RequestMapping("/updateUserShipping")
    public String updateUserShipping(
            @ModelAttribute("id") Long shippingAddressId, Principal principal, Model model
    ) {
        User user = userService.findByEmail(principal.getName());
        UserShipping userShipping = userShippingService.findById(shippingAddressId);

        if (user.getId() != userShipping.getUser().getId()) {
            return "badRequestPage";
        } else {
            model.addAttribute("user", user);

            model.addAttribute("userShipping", userShipping);

            List<String> stateList = USConstants.listOfUSStatesCode;
            Collections.sort(stateList);
            model.addAttribute("stateList", stateList);

            model.addAttribute("addNewShippingAddress", true);
            model.addAttribute("classActiveShipping", true);
            model.addAttribute("listOfCreditCards", true);

            model.addAttribute("userPaymentList", user.getUserPaymentList());
            model.addAttribute("userShippingList", user.getUserShippingList());
            model.addAttribute("orderList", user.getOrderList());

            return "myProfile";
        }
    }

    @PostMapping("/setDefaultPayment")
    public String setDefaultPayment(
            @ModelAttribute("defaultUserPaymentId") Long defaultPaymentId, Principal principal, Model model
    ) {
        User user = userService.findByEmail(principal.getName());
        userService.setUserDefaultPayment(defaultPaymentId, user);

        model.addAttribute("user", user);
        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("classActiveBilling", true);
        model.addAttribute("listOfShippingAddresses", true);

        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("orderList", user.getOrderList());

        return "myProfile";
    }

    @PostMapping("/setDefaultShippingAddress")
    public String setDefaultShippingAddress(
            @ModelAttribute("defaultShippingAddressId") Long defaultShippingId, Principal principal, Model model
    ) {
        User user = userService.findByEmail(principal.getName());
        userService.setUserDefaultShipping(defaultShippingId, user);

        model.addAttribute("user", user);
        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("classActiveShipping", true);
        model.addAttribute("listOfShippingAddresses", true);

        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("orderList", user.getOrderList());

        return "myProfile";
    }

    @RequestMapping("/removeCreditCard")
    public String removeCreditCard(
            @ModelAttribute("id") Long creditCardId, Principal principal, Model model
    ) {
        User user = userService.findByEmail(principal.getName());
        UserPayment userPayment = userPaymentService.findById(creditCardId);

        if (user.getId() != userPayment.getUser().getId()) {
            return "badRequestPage";
        } else {
            model.addAttribute("user", user);
            userPaymentService.deleteById(creditCardId);

            model.addAttribute("listOfCreditCards", true);
            model.addAttribute("classActiveBilling", true);
            model.addAttribute("listOfShippingAddresses", true);

            model.addAttribute("userPaymentList", user.getUserPaymentList());
            model.addAttribute("userShippingList", user.getUserShippingList());
            model.addAttribute("orderList", user.getOrderList());

            return "myProfile";
        }
    }

    

    @RequestMapping("/orderDetail")
    public String orderDetail(
            @RequestParam("id") Long orderId,
            Principal principal, Model model
    ) {
        User user = userService.findByEmail(principal.getName());
        Order order = orderService.findById(orderId);

        if (!order.getUser().getId().equals(user.getId())) {
            return "badRequestPage";
        } else {
            List<CartItem> cartItemList = cartItemService.findByOrder(order);
            model.addAttribute("cartItemList", cartItemList);
            model.addAttribute("user", user);
            model.addAttribute("order", order);

            model.addAttribute("userPaymentList", user.getUserPaymentList());
            model.addAttribute("userShippingList", user.getUserShippingList());
            model.addAttribute("orderList", user.getOrderList());

            UserShipping userShipping = new UserShipping();
            model.addAttribute("userShipping", userShipping);

            List<String> stateList = USConstants.listOfUSStatesCode;
            Collections.sort(stateList);
            model.addAttribute("stateList", stateList);

            model.addAttribute("listOfShippingAddresses", true);
            model.addAttribute("classActiveOrders", true);
            model.addAttribute("listOfCreditCards", true);
            model.addAttribute("displayOrderDetail", true);

            return "myProfile";
        }
    }


}
