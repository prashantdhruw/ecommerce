package com.yourapp.ecommerce.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.yourapp.ecommerce.entity.Order;
import com.yourapp.ecommerce.entity.OrderStatus;
import com.yourapp.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/stripe")
public class WebhookController {

    @Value("${stripe.webhook.secret:whsec_test_secret}")
    private String endpointSecret;

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload,
                                                    @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
            if (paymentIntent != null) {
                Order order = orderService.findOrderByPaymentIntentId(paymentIntent.getId());
                orderService.updateOrderStatus(order.getId(), OrderStatus.PAID);
            }
        } else if ("payment_intent.payment_failed".equals(event.getType())) {
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
            if (paymentIntent != null) {
                Order order = orderService.findOrderByPaymentIntentId(paymentIntent.getId());
                orderService.updateOrderStatus(order.getId(), OrderStatus.FAILED);
            }
        }

        return ResponseEntity.ok("Received");
    }
}