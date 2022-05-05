package com.garden.treehouse.controller;

import com.garden.treehouse.model.*;
import com.garden.treehouse.repos.PaymentRepository;
import com.garden.treehouse.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;


@Controller
public class CheckoutController {

    private final ShippingAddress shippingAddress = new ShippingAddress();

    private final UserService userService;
    private final CartItemService cartItemService;
    private final ShippingAddressService shippingAddressService;
    private final OrderService orderService;
    private final ShoppingCartService shoppingCartService;

    private final PaymentRepository paymentRepository;

    public CheckoutController(UserService userService,
                              CartItemService cartItemService, ShippingAddressService shippingAddressService,
                              OrderService orderService, ShoppingCartService shoppingCartService,
                              PaymentRepository paymentRepository) {
        this.userService = userService;
        this.cartItemService = cartItemService;
        this.shippingAddressService = shippingAddressService;
        this.orderService = orderService;
        this.shoppingCartService = shoppingCartService;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/checkout")
    public String checkout(
            @RequestParam("id") Long cartId,
            @RequestParam(name = "error", required = false) Boolean error,
            @RequestParam(name = "inStock", required = false) Boolean inStock,
            Model model, Principal principal
    ) {
        User user = userService.findByEmail(principal.getName());

        if (!cartId.equals(user.getShoppingCart().getId())) {
            return "badRequestPage";
        }

        var cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());
        var userShippingList = user.getUserShippingList();

        model.addAttribute("userShippingList", userShippingList);
        model.addAttribute("payment", new Payment());


        for (UserShipping userShipping : userShippingList) {
            if (userShipping.isUserShippingDefault()) {
                shippingAddressService.setByUserShipping(userShipping, shippingAddress);
            }
        }


        addAttributes(model, user, cartItemList);

        model.addAttribute("shippingMethod", "Ground");
        if (error == null)
            model.addAttribute("error", "Couldn't make payment.");
        else if (error)
            model.addAttribute("error", "Missing some required information");
        else if (inStock != null && !inStock)
            model.addAttribute("error", "Not enough product(s) in stock.");
        else
            model.addAttribute("error", null);
        return "checkout";

    }


    @RequestMapping("/pay")
    public String checkoutPost(@ModelAttribute("shippingAddress") ShippingAddress shippingAddress,
                               @ModelAttribute("shippingMethod") String shippingMethod, @ModelAttribute("payment") Payment payment,
                               Principal principal, Model model, HttpServletRequest request) throws Exception {
        ShoppingCart shoppingCart = userService.findByEmail(principal.getName()).getShoppingCart();
        User user = userService.findByEmail(principal.getName());

        System.out.println(shippingMethod);

        if (shippingAddress.getShippingAddressStreet().isEmpty() || shippingAddress.getShippingAddressCity().isEmpty()
                || shippingAddress.getShippingAddressState().isEmpty()
                || shippingAddress.getShippingAddressPostcode().isEmpty()) {
            return "redirect:checkout?id=" + shoppingCart.getId() + "&error=true";
        }

        var payments = paymentRepository.findAll();
        boolean check = true;
        for (var pay: payments) {
            if (payment.getType().equals(pay.getType()))
                if (payment.getCardNumber().equals(pay.getCardNumber()))
                    if (payment.getCvc() == pay.getCvc())
                        if (payment.getExpiryMonth() == pay.getExpiryMonth())
                            if (payment.getExpiryYear() == pay.getExpiryYear())
                                check = false;
        }

        if (check)
            return "redirect:checkout?id=" + shoppingCart.getId();

        var amount = 0;
        var cartTotal = shoppingCart.getGrandTotal().doubleValue();
        if (cartTotal >= 50.00)
            cartTotal = shoppingCart.getDiscount().doubleValue();

        amount += (cartTotal + (cartTotal * 0.1));

        if (shippingMethod.equals("Ground"))
            amount += 10;
        else
            amount += 30;

        shoppingCart.setGrandTotal(BigDecimal.valueOf(amount));
        Order order = orderService.createOrder(shoppingCart, shippingAddress, shippingMethod, payment, user);
        if (order == null)
            return "redirect:checkout?id=" + shoppingCart.getId() + "&inStock=false";


        shoppingCartService.clearShoppingCart(shoppingCart);
        model.addAttribute("cartItemList", order.getCartItemList());

        LocalDate today = LocalDate.now();
        LocalDate estimatedDeliveryDate;
        if (shippingMethod.equals("Ground")) {
            estimatedDeliveryDate = today.plusDays(10);
        } else {
            estimatedDeliveryDate = today.plusDays(3);
        }

        shoppingCartService.clearShoppingCart(shoppingCart);
        model.addAttribute("estimatedDeliveryDate", estimatedDeliveryDate);

        return "orderSubmittedPage";

    }



    private void addAttributes(Model model, User user, List<CartItem> cartItemList) {
        model.addAttribute("shippingAddress", shippingAddress);
        model.addAttribute("cartItemList", cartItemList);
        model.addAttribute("shoppingCart", user.getShoppingCart());

    }
}
