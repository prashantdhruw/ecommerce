package com.ecommerce.service;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.ecommerce.entity.Order;
import com.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Autowired
    private OrderRepository orderRepository;

    public String createPaymentIntent(Order order) {
        log.info("Creating payment intent for order ID: {}", order.getId());
        Stripe.apiKey = stripeSecretKey;

        try {
            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(order.getTotalAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue()) // amount in cents
                            .setCurrency("usd")
                            .putMetadata("order_id", order.getId().toString())
                            .build();

            PaymentIntent intent = PaymentIntent.create(params);

            order.setPaymentIntentId(intent.getId());
            orderRepository.save(order);

            log.info("Payment intent created with ID: {} for order ID: {}", intent.getId(), order.getId());
            return intent.getClientSecret();
        } catch (Exception e) {
            log.error("Stripe payment intent creation failed for order ID: {}", order.getId(), e);
            throw new RuntimeException("Stripe payment intent creation failed", e);
        }
    }
}