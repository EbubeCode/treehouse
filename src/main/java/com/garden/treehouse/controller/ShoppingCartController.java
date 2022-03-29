package com.garden.treehouse.controller;

import com.garden.treehouse.model.CartItem;
import com.garden.treehouse.model.ShoppingCart;
import com.garden.treehouse.model.User;
import com.garden.treehouse.services.CartItemService;
import com.garden.treehouse.services.ProductService;
import com.garden.treehouse.services.ShoppingCartService;
import com.garden.treehouse.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Controller
public class ShoppingCartController {
    private final UserService userService;
    private final CartItemService cartItemService;
    private final ProductService productService;
    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(UserService userService, CartItemService cartItemService, ProductService productService, ShoppingCartService shoppingCartService) {
        this.userService = userService;
        this.cartItemService = cartItemService;
        this.productService = productService;
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("/cart")
    public String shoppingCart(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        ShoppingCart shoppingCart = user.getShoppingCart();

        List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);

        shoppingCartService.updateShoppingCart(shoppingCart);

        model.addAttribute("cartItems", cartItemList);
        model.addAttribute("shoppingCart", shoppingCart);

        return "shoppingCart";
    }

    @PostMapping("/addItem/{pid}/{qty}")
    @ResponseBody
    public String addItem(@PathVariable Long pid, @PathVariable int qty, Principal principal) {
        if (principal == null) return "You must login to add to cart.";
        var user = userService.findByEmail(principal.getName());
        System.out.println(user.getEmail());
        var product = productService.findById(pid);

        if (qty > product.getInStockNumber()) {
            return "Not enough products in stock";
        }

        var response = cartItemService.addProductToCartItem(product, user, qty);

        return response;
    }

    @PostMapping("/updateCartItem/{id}/{qty}")
    @ResponseBody
    public String updateShoppingCart(@PathVariable Long id, @PathVariable int qty, Principal principal) {
        var user = userService.findByEmail(principal.getName());
        CartItem cartItem = cartItemService.findById(id);

        cartItem.setQty(qty);
        cartItemService.updateCartItem(cartItem);
        shoppingCartService.updateShoppingCart(user.getShoppingCart());

        return cartItem.getQty() + "";
    }

    @DeleteMapping("/removeCartItem")
    @ResponseBody
    public Long removeItem(@RequestParam("id") Long id, Principal principal) {
        var user = userService.findByEmail(principal.getName());

        cartItemService.removeCartItem(cartItemService.findById(id));
        shoppingCartService.updateShoppingCart(user.getShoppingCart());

        return id;
    }

}
