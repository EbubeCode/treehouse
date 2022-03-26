package com.garden.treehouse.controller;

import com.garden.treehouse.model.Product;
import com.garden.treehouse.model.User;
import com.garden.treehouse.services.ProductService;
import com.garden.treehouse.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Controller
public class SearchController {
	private final UserService userService;
	private final ProductService productService;

	public SearchController(UserService userService, ProductService productService) {
		this.userService = userService;
		this.productService = productService;
	}

	@GetMapping("/searchByCategory")
	public String searchByCategory(
			@RequestParam("category") String category,
			Model model, Principal principal
			){
		if(principal!=null) {
			String username = principal.getName();
			User user = userService.findByEmail(username);
			model.addAttribute("user", user);
		}
		

		List<Product> products ;
		if (category.equalsIgnoreCase("all"))
			products = productService.findAll();
		else
			products = productService.findByCategory(category);
		
		if (products.isEmpty()) {
			model.addAttribute("emptyList", true);
			return "productRack";
		}
		
		model.addAttribute("products", products);
		
		return "productRack";
	}
	
	@PostMapping("/searchProduct")
	public String searchBook(
			@ModelAttribute("keyword") String keyword,
			Principal principal, Model model
			) {
		if(principal!=null) {
			String username = principal.getName();
			User user = userService.findByEmail(username);
			model.addAttribute("user", user);
		}
		
		List<Product> products = productService.blurrySearch(keyword);
		
		if (products.isEmpty()) {
			System.out.println("products is empty " + products);
			model.addAttribute("emptyList", true);
			return "productRack";
		}
		
		model.addAttribute("products", products);
		
		return "productRack";
	}
}
