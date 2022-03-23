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
		
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("shoppingCart", shoppingCart);
		
		return "shoppingCart";
	}
	
	@PostMapping("/addItem/{pid}/{qty}")
	@ResponseBody
	public String addItem(@PathVariable Long pid,
			@PathVariable int qty,
			Principal principal
			) {
		if (principal == null)
			return "You must login to add to cart.";
		var user = userService.findByEmail(principal.getName());
		System.out.println(user.getEmail());
		var product = productService.findById(pid);
		
		if (qty > product.getInStockNumber()) {
			return "Not enough products in stock";
		}
		
		CartItem cartItem = cartItemService.addProductToCartItem(product, user, qty);

		return "product successfully added to shopping cart";
	}
	
	@RequestMapping("/updateCartItem")
	public String updateShoppingCart(
			@ModelAttribute("id") Long cartItemId,
			@ModelAttribute("qty") int qty
			) {
		CartItem cartItem = cartItemService.findById(cartItemId);
		cartItem.setQty(qty);
		cartItemService.updateCartItem(cartItem);
		
		return "forward:/shoppingCart/cart";
	}
	
	@RequestMapping("/removeItem")
	public String removeItem(@RequestParam("id") Long id) {
		cartItemService.removeCartItem(cartItemService.findById(id));
		
		return "forward:/shoppingCart/cart";
	}

}
