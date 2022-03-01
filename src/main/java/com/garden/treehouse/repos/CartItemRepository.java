package com.garden.treehouse.repos;

import com.garden.treehouse.model.CartItem;
import com.garden.treehouse.model.Order;
import com.garden.treehouse.model.ShoppingCart;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface CartItemRepository extends CrudRepository<CartItem, Long>{
	
	Optional<List<CartItem>> findByShoppingCart(ShoppingCart shoppingCart);
	
	Optional<List<CartItem>> findByOrder(Order order);
}
