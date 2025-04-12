package com.yourapp.ecommerce.controller;

import com.yourapp.ecommerce.dto.ShippingDetailsDto;
import com.yourapp.ecommerce.entity.Order;
import com.yourapp.ecommerce.entity.User;
import com.yourapp.ecommerce.service.OrderService;
import com.yourapp.ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@AuthenticationPrincipal User user,
                                      @Valid @RequestBody ShippingDetailsDto shippingDetails) {
        Order order = orderService.createOrderFromCart(user, shippingDetails);
        String clientSecret = paymentService.createPaymentIntent(order);

        return ResponseEntity.ok().body(
                java.util.Map.of(
                        "orderId", order.getId(),
                        "clientSecret", clientSecret
                )
        );
    }

    @GetMapping("/")
    public ResponseEntity<List<Order>> getUserOrders(@AuthenticationPrincipal User user) {
        List<Order> orders = orderService.getOrdersByUser(user);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId,
                                          @AuthenticationPrincipal User user) {
        Order order = orderService.getOrderById(orderId, user);
        return ResponseEntity.ok(order);
    }
}