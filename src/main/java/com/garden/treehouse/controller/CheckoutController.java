package com.garden.treehouse.controller;

import com.garden.treehouse.events.OrderCreated;
import com.garden.treehouse.model.*;
import com.garden.treehouse.payment.Payload;
import com.garden.treehouse.payment.PaymentService;
import com.garden.treehouse.services.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Controller
public class CheckoutController {

    private final ShippingAddress shippingAddress = new ShippingAddress();

    private final UserService userService;
    private final CartItemService cartItemService;
    private final ShippingAddressService shippingAddressService;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final ShoppingCartService shoppingCartService;
    private final ApplicationEventPublisher eventPublisher;

    public CheckoutController(UserService userService,
                              CartItemService cartItemService, ShippingAddressService shippingAddressService,
                              PaymentService paymentService, OrderService orderService, ShoppingCartService shoppingCartService, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.cartItemService = cartItemService;
        this.shippingAddressService = shippingAddressService;
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.shoppingCartService = shoppingCartService;
        this.eventPublisher = eventPublisher;
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
        else if (!inStock)
            model.addAttribute("error", "Not enough product(s) in stock.");
        else
            model.addAttribute("error", null);
        return "checkout";

    }


    @RequestMapping("/pay")
    public String checkoutPost(@ModelAttribute("shippingAddress") ShippingAddress shippingAddress,
                               @ModelAttribute("shippingMethod") String shippingMethod,
                               Principal principal, Model model, HttpServletRequest request) throws Exception {
        ShoppingCart shoppingCart = userService.findByEmail(principal.getName()).getShoppingCart();
        User user = userService.findByEmail(principal.getName());

        if (request.getMethod().contentEquals("GET")) {
            //This happens in case of a cancelling payment. This redirects to the checkout page.
            orderService.deleteOrder(user);
            return "redirect:checkout?id=" + shoppingCart.getId();
        }

        if (shippingAddress.getShippingAddressStreet().isEmpty() || shippingAddress.getShippingAddressCity().isEmpty()
                || shippingAddress.getShippingAddressState().isEmpty()
                || shippingAddress.getShippingAddressZipcode().isEmpty()) {
            return "redirect:checkout?id=" + shoppingCart.getId() + "&error=true";
        }


        Order order = orderService.createOrder(shoppingCart, shippingAddress, shippingMethod, user);
        if (order == null)
            return "redirect:checkout?id=" + shoppingCart.getId() + "&inStock=false";

        var amount = 0;
        var cartTotal = shoppingCart.getGrandTotal().doubleValue();
        if (cartTotal < 50.00)
            amount += (cartTotal + (cartTotal * 0.1));
        else {
            cartTotal = shoppingCart.getDiscount().doubleValue();
            amount += (cartTotal + (cartTotal * 0.1));
        }

        if (shippingMethod.equals("Ground"))
            amount += 10;
        else
            amount += 30;

        model.addAttribute("payload", getPayload(user, amount, shippingAddress.getShippingAddressCountry()));
        paymentService.initialize(model, order.getId());
        return "payment/pay";

    }

    @GetMapping("/callback/{id}")
    public String callback(@RequestParam Map<String, String> params, @PathVariable Long id, Model model,
                           Principal principal) {
        String transactionId = params.get("transaction_id");
        Map<String, Object> transactionData = paymentService.verifyTransaction(transactionId);

        var user = userService.findByEmail(principal.getName());
        ShoppingCart shoppingCart = user.getShoppingCart();
        if (transactionData.get("status").equals("success")) {
            var order = orderService.findById(id);
            order.setOrderStatus("created");
            var shippingMethod = orderService.save(order).getShippingMethod();
            LocalDate today = LocalDate.now();
            LocalDate estimatedDeliveryDate;
            if (shippingMethod.equals("groundShipping")) {
                estimatedDeliveryDate = today.plusDays(10);
            } else {
                estimatedDeliveryDate = today.plusDays(3);
            }
            var cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());

            eventPublisher.publishEvent(new OrderCreated(user, order));
            shoppingCartService.clearShoppingCart(shoppingCart);
            model.addAttribute("estimatedDeliveryDate", estimatedDeliveryDate);

            model.addAttribute("cartItemList", cartItemList);
            return "orderSubmittedPage";

        }

        return "redirect:checkout?id=" + shoppingCart.getId();
    }

    private Payload getPayload(User user, double amount, String country) {
        var email = user.getEmail().equals("admin") ? "chukwuma258@gmail.com" : user.getEmail();

        var payload = new Payload();
        payload.setAmount(String.format("%.2f", (amount / 0.0022)));
        payload.setPayment_method("card");
        payload.setDescription("Order payment");
        payload.setCountry(switchCountry(country));
        payload.setCurrency("NGN");
        payload.getCustomer().setEmail(email);
        payload.getCustomer().setName(user.getFirstName() + " " + user.getLastName());

        return payload;
    }

    private String switchCountry(String country) {
        return switch (country) {
            case "Nigeria" -> "NG";
            case "UK" -> "UK";
            case "US" -> "US";
            case "Ghana" -> "GH";
            case "Germany" -> "DE";
            default -> "FR";
        };
    }


    private void addAttributes(Model model, User user, List<CartItem> cartItemList) {
        model.addAttribute("shippingAddress", shippingAddress);
        model.addAttribute("cartItemList", cartItemList);
        model.addAttribute("shoppingCart", user.getShoppingCart());

    }
}
