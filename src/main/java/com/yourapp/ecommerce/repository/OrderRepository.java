package com.yourapp.ecommerce.repository;

import com.yourapp.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByPaymentIntentId(String paymentIntentId);

    // Admin: find orders by status
    List<Order> findByStatus(com.yourapp.ecommerce.entity.OrderStatus status);
}
