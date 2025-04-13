package com.ecommerce.service;

import com.ecommerce.dto.ShippingDetailsDto;
import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order createOrderFromCart(User user, ShippingDetailsDto shippingDetails) {
        log.info("Creating order for user: {}", user.getUsername());
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            log.warn("Attempted to create order with empty cart for user: {}", user.getUsername());
            throw new RuntimeException("Cart is empty");
        }

        for (CartItem cartItem : cart.getItems()) {
            if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
                log.warn("Insufficient stock for product {} requested by user {}", cartItem.getProduct().getName(), user.getUsername());
                throw new RuntimeException("Insufficient stock for product: " + cartItem.getProduct().getName());
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(shippingDetails.getAddress());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            orderItems.add(orderItem);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(total);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        cartItemRepository.deleteAll(cart.getItems());

        log.info("Order created with ID: {} for user: {}", savedOrder.getId(), user.getUsername());
        return savedOrder;
    }

    public Order getOrderById(Long orderId, User user) {
        return orderRepository.findById(orderId)
                .filter(order -> order.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void updateOrderStatus(Long orderId, OrderStatus status) {
        log.info("Updating order ID {} to status {}", orderId, status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
        log.info("Order ID {} updated to status {}", orderId, status);
    }

    public Order findOrderByPaymentIntentId(String paymentIntentId) {
        return orderRepository.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public java.util.List<Order> getOrdersByUser(User user) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(user.getId()))
                .toList();
    }

    // Admin: get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Admin: get orders filtered by status
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // Admin: get order by ID without user restriction
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}