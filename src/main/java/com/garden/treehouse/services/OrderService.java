package com.garden.treehouse.services;

import com.garden.treehouse.model.*;
import com.garden.treehouse.repos.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;


@Service
@Transactional
public class OrderService{

	private final OrderRepository orderRepository;
	private final CartItemService cartItemService;

	public OrderService(OrderRepository orderRepository, CartItemService cartItemService) {
		this.orderRepository = orderRepository;
		this.cartItemService = cartItemService;
	}

	public Order createOrder(ShoppingCart shoppingCart,
										  ShippingAddress shippingAddress,
										  String shippingMethod,
										  User user) {
		var opOrder = orderRepository.findOrderByUserAndOrderStatus(user, "pending");
		if(opOrder.isPresent())
			return opOrder.get();
		Order order = new Order();
		order.setOrderStatus("pending");
		order.setShippingAddress(shippingAddress);
		order.setShippingMethod(shippingMethod);
		
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		
		for(CartItem cartItem : cartItemList) {
			Product product = cartItem.getProduct();
			cartItem.setOrder(order);
			product.setInStockNumber(product.getInStockNumber() - cartItem.getQty());
		}
		
		order.setCartItemList(cartItemList);
		order.setOrderDate(Calendar.getInstance().getTime());
		order.setOrderTotal(shoppingCart.getGrandTotal());
		shippingAddress.setOrder(order);
		order.setUser(user);
		order = orderRepository.save(order);
		
		return order;
	}
	
	public Order findById(Long id) {
		return orderRepository.findById(id).orElse(null);
	}

	public Order save(Order order) {
		return orderRepository.save(order);
	}

}
