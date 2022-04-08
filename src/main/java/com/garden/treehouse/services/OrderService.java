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

		Order order = new Order();
		order.setOrderStatus("pending");
		order.setShippingAddress(shippingAddress);
		order.setShippingMethod(shippingMethod == null ? "Ground": shippingMethod);
		
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		
		for(CartItem cartItem : cartItemList) {
			Product product = cartItem.getProduct();
			cartItem.setOrder(order);
			if (product.getInStockNumber() < cartItem.getQty())
				return null;
			product.setInStockNumber(product.getInStockNumber() - cartItem.getQty());
			if (product.getInStockNumber()  <= 0)
				product.setActive(false);

		}
		
		order.setCartItemList(cartItemList);
		order.setOrderDate(Calendar.getInstance().getTime());
		order.setOrderTotal(shoppingCart.getGrandTotal());
		shippingAddress.setOrder(order);
		order.setUser(user);
		order = orderRepository.save(order);
		
		return order;
	}

	public void deleteOrder(User user) {
		var opOrder = orderRepository.findOrderByUserAndOrderStatus(user, "pending");
		if(opOrder.isPresent()) {
			var order = opOrder.get();
			order.getCartItemList().forEach(c -> {
				var newQty = c.getProduct().getInStockNumber() + c.getQty();
				c.getProduct().setInStockNumber(newQty);
				c.getProduct().setActive(true);
			});
			order = orderRepository.save(order);
			orderRepository.delete(order);
		}
	}

	public Order findById(Long id) {
		return orderRepository.findById(id).orElse(null);
	}

	public Order save(Order order) {
		return orderRepository.save(order);
	}

}
