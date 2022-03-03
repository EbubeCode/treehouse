package com.garden.treehouse.services;

import com.garden.treehouse.model.*;
import com.garden.treehouse.repos.CartItemRepository;
import com.garden.treehouse.repos.ProductToCartItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductToCartItemRepository productToCartItemRepository;

    public CartItemService(CartItemRepository cartItemRepository, ProductToCartItemRepository productToCartItemRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productToCartItemRepository = productToCartItemRepository;
    }

    public List<CartItem> findByShoppingCart(ShoppingCart shoppingCart) {
        return cartItemRepository.findByShoppingCart(shoppingCart).orElse(List.of());

    }

    public CartItem updateCartItem(CartItem cartItem) {
        BigDecimal bigDecimal = new BigDecimal(cartItem.getProduct().getOurPrice()).multiply(new BigDecimal(cartItem.getQty()));

        bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
        cartItem.setSubtotal(bigDecimal);

        cartItemRepository.save(cartItem);

        return cartItem;
    }

    public CartItem addBookToCartItem(Product product, User user, int qty) {
        List<CartItem> cartItemList = findByShoppingCart(user.getShoppingCart());

        for (CartItem cartItem : cartItemList) {
            if (product.getId() == cartItem.getProduct().getId()) {
                cartItem.setQty(cartItem.getQty() + qty);
                cartItem.setSubtotal(new BigDecimal(product.getOurPrice()).multiply(new BigDecimal(qty)));
                cartItemRepository.save(cartItem);
                return cartItem;
            }
        }

        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(user.getShoppingCart());
        cartItem.setProduct(product);

        cartItem.setQty(qty);
        cartItem.setSubtotal(BigDecimal.valueOf(product.getOurPrice()).multiply(new BigDecimal(qty)));
        cartItem = cartItemRepository.save(cartItem);

        ProductToCartItem productToCartItem = new ProductToCartItem();
        productToCartItem.setProduct(product);
        productToCartItem.setCartItem(cartItem);
        productToCartItemRepository.save(productToCartItem);

        return cartItem;
    }

    public CartItem findById(Long id) {
        return cartItemRepository.findById(id).orElse(null);
    }

    public void removeCartItem(CartItem cartItem) {
        productToCartItemRepository.deleteByCartItem(cartItem);
        cartItemRepository.delete(cartItem);
    }

    public CartItem save(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    public List<CartItem> findByOrder(Order order) {
        return cartItemRepository.findByOrder(order).orElse(List.of());
    }

}
