# Product Context

This document provides a high-level overview of the ecommerce application codebase, including its structure, main components, and configuration.

## Project Overview

This is a Spring Boot-based ecommerce application with JWT authentication, Stripe payment integration, and a PostgreSQL backend. The application supports user registration, authentication, product management, cart and order processing, and admin features.

## Main Components

- **Entry Point:**  
  - `EcommerceAppApplication.java`: Main Spring Boot application class.

- **Configuration:**  
  - `SecurityConfig.java`: Configures JWT security, password encoding, and endpoint access.

- **Controllers:**  
  - `AdminOrderController.java`, `AdminProductController.java`, `AdminUserController.java`: Admin endpoints for managing orders, products, and users.
  - `AuthController.java`: User registration and authentication.
  - `CartController.java`: Cart management for authenticated users.
  - `OrderController.java`: User order management and checkout.
  - `ProductController.java`: Public product listing and details.
  - `WebhookController.java`: Stripe webhook endpoint for payment events.

- **DTOs:**  
  - AddItemRequest, AuthResponse, CartDto, CartItemDto, CreateProductRequest, LoginRequest, OrderDto, OrderItemDto, ProductDto, ShippingDetailsDto, SignupRequest, UpdateItemRequest, UpdateOrderStatusRequest, UpdateProductRequest, UpdateUserRoleRequest.

- **Entities:**  
  - Cart, CartItem, Category, Order, OrderItem, OrderStatus (enum), Product, User.

- **Exceptions:**  
  - GlobalExceptionHandler, InsufficientStockException, ResourceNotFoundException, ValidationException.

- **Repositories:**  
  - CartItemRepository, CartRepository, CategoryRepository, OrderItemRepository, OrderRepository, ProductRepository, UserRepository.

- **Security:**  
  - CustomUserDetailsService, JwtRequestFilter, JwtUtil.

- **Services:**  
  - CartService, OrderService, PaymentService, ProductService.

- **Configuration:**  
  - `application.properties`: Database, JWT, and Stripe configuration.

- **Test:**  
  - `EcommerceAppApplicationTests.java`: Spring Boot context load test.

## Notes

- All source code has been read and indexed for persistent context.
- See `activeContext.md` for current focus and recent changes.

---

[2025-04-11 06:18:57] - Full codebase inventory and structure captured. All source, config, and test files have been read for persistent context.