package com.yourapp.ecommerce.service;

import com.yourapp.ecommerce.dto.CartDto;
import com.yourapp.ecommerce.dto.CartItemDto;
import com.yourapp.ecommerce.entity.*;
import com.yourapp.ecommerce.repository.CartItemRepository;
import com.yourapp.ecommerce.repository.CartRepository;
import com.yourapp.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public CartDto getCart(User user) {
        Cart cart = getOrCreateCart(user);
        List<CartItem> cartItems = cartItemRepository.findAll().stream()
                .filter(item -> item.getCart().getId().equals(cart.getId()))
                .toList();

        List<CartItemDto> itemDtos = new ArrayList<>();
        double total = 0.0;

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            double price = product.getPrice().doubleValue();
            int quantity = item.getQuantity();
            total += price * quantity;

            itemDtos.add(new CartItemDto(
                    product.getId(),
                    product.getName(),
                    price,
                    quantity
            ));
        }

        return new CartDto(itemDtos, total);
    }

    public void addProductToCart(User user, Long productId, int quantity) {
        log.info("Adding product {} (qty {}) to cart for user {}", productId, quantity, user.getUsername());
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProduct(cart, product);
        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
            log.info("Updated quantity of product {} in cart for user {}", productId, user.getUsername());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
            log.info("Added new product {} to cart for user {}", productId, user.getUsername());
        }
    }

    public void updateCartItemQuantity(User user, Long productId, int quantity) {
        log.info("Updating quantity of product {} to {} in cart for user {}", productId, quantity, user.getUsername());
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        log.info("Updated quantity of product {} to {} in cart for user {}", productId, quantity, user.getUsername());
    }

    public void removeProductFromCart(User user, Long productId) {
        log.info("Removing product {} from cart for user {}", productId, user.getUsername());
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartItemRepository.delete(item);
        log.info("Removed product {} from cart for user {}", productId, user.getUsername());
    }

    public void clearCart(User user) {
        log.info("Clearing cart for user {}", user.getUsername());
        Cart cart = getOrCreateCart(user);
        List<CartItem> cartItems = cartItemRepository.findAll().stream()
                .filter(item -> item.getCart().getId().equals(cart.getId()))
                .toList();

        cartItemRepository.deleteAll(cartItems);
        log.info("Cleared cart for user {}", user.getUsername());
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }
}