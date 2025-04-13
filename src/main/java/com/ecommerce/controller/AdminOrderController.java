package com.ecommerce.controller;

import com.ecommerce.dto.OrderDto;
import com.ecommerce.dto.UpdateOrderStatusRequest;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/orders")
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<OrderDto> getAllOrders(@RequestParam(value = "status", required = false) OrderStatus status) {
        List<Order> orders;
        if (status != null) {
            orders = orderService.getOrdersByStatus(status);
        } else {
            orders = orderService.getAllOrders();
        }
        return orders.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return convertToDto(order);
    }

    @PutMapping("/{orderId}/status")
    public void updateOrderStatus(@PathVariable Long orderId,
                                  @Valid @RequestBody UpdateOrderStatusRequest request) {
        orderService.updateOrderStatus(orderId, request.getStatus());
    }

    private OrderDto convertToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPaymentIntentId(order.getPaymentIntentId());
        dto.setItems(
                order.getOrderItems().stream().map(orderItem -> {
                    com.ecommerce.dto.OrderItemDto itemDto = new com.ecommerce.dto.OrderItemDto();
                    itemDto.setProductId(orderItem.getProduct().getId());
                    itemDto.setProductName(orderItem.getProduct().getName());
                    itemDto.setQuantity(orderItem.getQuantity());
                    itemDto.setPriceAtPurchase(orderItem.getPriceAtPurchase());
                    return itemDto;
                }).collect(Collectors.toList())
        );
        return dto;
    }
}