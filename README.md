# EcommerceApp

A RESTful ecommerce backend application built with **Spring Boot 3**, using JWT-based authentication, role-based authorization, Stripe integration for payments, OpenAPI (Swagger) documentation, and PostgreSQL as the database.

---

## Features

- **Authentication & Authorization**:  
  JWT-secured endpoints with roles (`CUSTOMER`, `ADMIN`).
- **User management**:  
  Registration, login, and admin role management.
- **Product catalog**:  
  CRUD for products, pagination, rich search & filtering.
- **Categories**:  
  Assign products to categories; manage categories (admin).
- **Shopping Cart**:  
  Each user has a cart, can add/update/remove items.
- **Order Management**:  
  Place orders, view order history, and admin order management.
- **Stripe Integration**:  
  Create payment intents; Stripe webhooks to update payment/order status.
- **Error handling**:  
  Consistent, descriptive API error responses.
- **API documentation**:  
  Auto-generated with Swagger UI (OpenAPI).

---

## Technology Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL**
- **JWT (jjwt)**
- **Stripe Java SDK**
- **Lombok**
- **OpenAPI (springdoc-openapi)**
- **JUnit** for testing

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL
- [Stripe account](https://dashboard.stripe.com/register) (for real payments, optional)

### Clone & Configure

```sh
git clone https://github.com/yourusername/prashantdhruw-ecommerce.git
cd prashantdhruw-ecommerce
```

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password

jwt.secret=your_512_bit_secret
jwt.expiration=3600000

stripe.secret.key=your_stripe_secret_key
stripe.public.key=your_stripe_public_key
stripe.webhook.secret=your_webhook_signing_secret
```

> **Note:** _For development, you can use your own Stripe test keys. Webhook secret is needed if you use Stripe's local webhook testing (`stripe listen`)._

---

### Build & Run

#### Run with Maven

```sh
mvn spring-boot:run
```

Or build and run the JAR:

```sh
mvn clean package
java -jar target/ecommerceApi-0.0.1-SNAPSHOT.jar
```

---

## API Documentation

After running, browse [Swagger UI](http://localhost:8080/swagger-ui/index.html)  
All endpoints are documented and testable via Swagger.

---

## API Endpoints Overview

Short summary of main endpoint groups (see OpenAPI/Swagger for full details):

### Auth
- `POST /api/auth/signup` — User signup (role MUST be `CUSTOMER`)
- `POST /api/auth/login` — Login, returns JWT

### Products
- `GET /api/products` — List (pagination, filter by category or search)
- `GET /api/products/{id}` — Product details  
- **Admin**:
  - `POST /api/admin/products`, `PUT`, `DELETE` — CRUD

### Categories
- `GET /api/categories` — List (Not shown above but likely in CategoryController)
- **Admin**:
  - `POST /api/admin/categories` — Add category

### Cart (Authenticated only)
- `GET /api/cart` — Get current user's cart
- `POST /api/cart/items` — Add to cart
- `PUT /api/cart/items/{productId}` — Update cart item qty
- `DELETE /api/cart/items/{productId}` — Remove from cart

### Orders (Authenticated only)
- `POST /api/orders/checkout` — Place order, creates Stripe PaymentIntent
- `GET /api/orders/` — List user's orders
- `GET /api/orders/{orderId}` — Order details

- **Admin**:
  - `GET /api/admin/orders` — List (optionally filter by status)
  - `PUT /api/admin/orders/{id}/status` — Update status

### Admin: User Management
- `GET /api/admin/users` — List users
- `PUT /api/admin/users/{userId}/role` — Update user roles

### Stripe Webhooks
- `POST /api/webhooks/stripe` — Stripe calls this to update payment/order status  
  (Configure your `endpointSecret` in properties).

---

## Database Schema

- **User** (id, username, email, password, roles)
- **Product** (id, name, description, price, stock, category)
- **Category** (id, name)
- **Cart** (id, user)
- **CartItem** (id, cart, product, quantity)
- **Order** (id, user, status, totalAmount, shipping, paymentIntent)
- **OrderItem** (id, order, product, quantity, priceAtPurchase)

Schema auto-generates via JPA (`ddl-auto=update`).

---

## Stripe Integration

- On checkout, a Stripe PaymentIntent is created.  
- PaymentIntent ID is stored with the order.
- Stripe sends webhook events to `/api/webhooks/stripe` for successful/failed payments, which update the corresponding order.

---

## Security

- **JWT Bearer** for all protected resources.
- _Only users with `ADMIN` role can access `/api/admin/**` endpoints._
- Public endpoints:  
  - `/api/auth/**`, `/api/products/**`

---

## Testing

- Run tests with:
  ```sh
  mvn test
  ```
- Main test class:  
  `src/test/java/com/yourapp/ecommerce/EcommerceAppApplicationTests.java`

You can extend this with your own unit/integration tests.

---

## Customizing / Extending

- Modular service & repository structure
- DTOs for request/response separation
- Exception handling (`GlobalExceptionHandler`)
- Easily add more roles, enrich product/model, or extend payment logic

---

## License

This project is for demo/learning purposes; no license specified.

---

## Author

Developed by [Prashant Dhruw](https://github.com/prashantdhruw)  
Would you like to contribute or open issues? Fork or PR welcome!

---

## Questions?

Open an [issue](https://github.com/yourusername/prashantdhruw-ecommerce/issues) or [contact the author](mailto:prashantdhruw673@gmail.com).

---

**Happy Coding!**
