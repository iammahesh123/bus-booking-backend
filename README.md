# BlueBus Backend - Spring Boot

This is the backend service for the BlueBus web-based bus booking application. It is built using **Spring Boot** and provides REST APIs for managing buses, routes, schedules, bookings, and user authentication.

---

## Features

- Admin functionalities:
  - Bus management (add/update/delete)
  - Route and schedule management
  - View/manage customer bookings
- Customer functionalities:
  - Search available buses
  - Book seats
  - Register/login and manage bookings
- Secure authentication (JWT-based)
- Integration-ready payment and email services

---

## Technologies Used

- **Language**: Java
- **Framework**: Spring Boot
- **Database**: MySQL / PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security with JWT
- **Build Tool**: Maven / Gradle
- **Other**: Lombok, Swagger (OpenAPI)

---

## Prerequisites

- Java 17+
- Maven or Gradle
- MySQL or PostgreSQL
- IDE (IntelliJ IDEA / Eclipse)
- Postman (for testing APIs)

---

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/iammahesh123/bus-booking-backend.git
cd bus-booking-backend
```

### 2. Configure Application Properties

Edit `src/main/resources/application.properties` or `application.yml`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/bluebus
spring.datasource.username=root
spring.datasource.password=your_password

# JPA Config
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Secret
app.jwt.secret=your_jwt_secret

# Server Port
server.port=8080
```

### 3. Run Database Migrations (if using Flyway/Liquibase) => not sure

```bash
# Example for Flyway (if configured)
mvn flyway:migrate
```

### 4. Build and Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or using Gradle
./gradlew bootRun
```

---

## API Documentation

Once the application is running, access Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

---

## Payment Gateway Configuration (not sure update later)

- Configure keys and callback URLs in `application.properties`.
- Create a `PaymentService` for integration with Razorpay, Stripe, etc.

---

## Email Service Setup (Optional)

Configure email in `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_email_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## API Endpoints (not sure update later)

| Method | Endpoint                 | Description                    |
|--------|--------------------------|--------------------------------|
| GET    | `/api/buses`             | List all available buses       |
| POST   | `/api/bookings`          | Create a new booking           |
| POST   | `/api/auth/register`     | Register a new user            |
| POST   | `/api/auth/login`        | Login and receive JWT          |
| GET    | `/api/admin/bookings`    | Admin: view all bookings       |

---

## Contribution Guidelines

- Fork the repository
- Create a feature branch
- Make your changes and commit
- Create a pull request

---

## License

Add it later

---

## Contact

For questions or support, contact maheshkadambala18@gmail.com
