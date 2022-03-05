package com.garden.treehouse.controller;

import com.garden.treehouse.model.CartItem;
import com.garden.treehouse.model.Product;
import com.garden.treehouse.model.ShoppingCart;
import com.garden.treehouse.model.User;
import com.garden.treehouse.services.CartItemService;
import com.garden.treehouse.services.ProductService;
import com.garden.treehouse.services.ShoppingCartService;
import com.garden.treehouse.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/shoppingCart")
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

	@RequestMapping("/cart")
	public String shoppingCart(Model model, Principal principal) {
		User user = userService.findByEmail(principal.getName());
		ShoppingCart shoppingCart = user.getShoppingCart();
		
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		
		shoppingCartService.updateShoppingCart(shoppingCart);
		
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("shoppingCart", shoppingCart);
		
		return "shoppingCart";
	}
	
	@RequestMapping("/addItem")
	public String addItem(
			@ModelAttribute("product") Product product,
			@ModelAttribute("qty") String qty,
			Model model, Principal principal
			) {
		User user = userService.findByEmail(principal.getName());
		product = productService.findById(product.getId());
		
		if (Integer.parseInt(qty) > product.getInStockNumber()) {
			model.addAttribute("notEnoughStock", true);
			return "forward:/productDetail?id="+product.getId();
		}
		
		CartItem cartItem = cartItemService.addBookToCartItem(product, user, Integer.parseInt(qty));
		model.addAttribute("addProductSucces", true);
		
		return "forward:/productDetail?id="+product.getId();
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
