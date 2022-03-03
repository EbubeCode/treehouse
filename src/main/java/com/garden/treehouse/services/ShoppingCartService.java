package com.garden.treehouse.services;

import com.garden.treehouse.model.CartItem;
import com.garden.treehouse.model.ShoppingCart;
import com.garden.treehouse.repos.ShoppingCartRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ShoppingCartService{
	
	private final CartItemService cartItemService;
	private final ShoppingCartRepository shoppingCartRepository;

	public ShoppingCartService(CartItemService cartItemService, ShoppingCartRepository shoppingCartRepository) {
		this.cartItemService = cartItemService;
		this.shoppingCartRepository = shoppingCartRepository;
	}

	public ShoppingCart updateShoppingCart(ShoppingCart shoppingCart) {
		BigDecimal cartTotal = new BigDecimal(0);
		
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		
		for (CartItem cartItem : cartItemList) {
			if(cartItem.getProduct().getInStockNumber() > 0) {
				cartItemService.updateCartItem(cartItem);
				cartTotal = cartTotal.add(cartItem.getSubtotal());
			}
		}
		
		shoppingCart.setGrandTotal(cartTotal);
		
		shoppingCartRepository.save(shoppingCart);
		
		return shoppingCart;
	}
	
	public void clearShoppingCart(ShoppingCart shoppingCart) {
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		
		for (CartItem cartItem : cartItemList) {
			cartItem.setShoppingCart(null);
			cartItemService.save(cartItem);
		}
		
		shoppingCart.setGrandTotal(new BigDecimal(0));
		
		shoppingCartRepository.save(shoppingCart);
	}

}

