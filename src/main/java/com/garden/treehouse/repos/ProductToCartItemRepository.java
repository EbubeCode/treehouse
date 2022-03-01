package com.garden.treehouse.repos;

import com.garden.treehouse.model.CartItem;
import com.garden.treehouse.model.ProductToCartItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ProductToCartItemRepository extends CrudRepository<ProductToCartItem, Long> {
	
	void deleteByCartItem(CartItem cartItem);

}
