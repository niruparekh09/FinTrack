## Backend Documentation

### Project Overview

A simple monolith Spring Boot backend for a personal finance tracker. The goal is to understand the *principles* of a production backend — not to over-engineer it.

**Estimated time: 6–7 hours across 3 days**

---

### Tech Stack

- **Spring Boot 3.x**
- **Spring Security** (JWT)
- **Spring Data JPA**
- **H2 Database** (in-memory, zero setup) — can swap to PostgreSQL later
- **Maven**

Use [start.spring.io](https://start.spring.io) with these dependencies: `Spring Web`, `Spring Security`, `Spring Data JPA`, `H2 Database`, `Lombok`, `Validation`

---

### Project Structure

```
src/main/java/com/fintrack/
│
├── config/
│   ├── SecurityConfig.java         # CORS, JWT filter chain
│   └── JwtConfig.java              # Secret key, expiry config
│
├── auth/
│   ├── AuthController.java         # /api/auth/register, /api/auth/login
│   ├── AuthService.java
│   ├── AuthRequest.java            # DTO: email + password
│   └── AuthResponse.java           # DTO: token + user info
│
├── user/
│   ├── User.java                   # Entity
│   └── UserRepository.java
│
├── transaction/
│   ├── Transaction.java            # Entity
│   ├── TransactionRepository.java
│   ├── TransactionController.java  # /api/transactions
│   ├── TransactionService.java
│   ├── TransactionRequest.java     # DTO
│   └── TransactionResponse.java    # DTO
│
├── summary/
│   ├── SummaryController.java      # /api/summary
│   └── SummaryService.java
│
└── security/
    ├── JwtUtil.java                # Generate + validate tokens
    ├── JwtAuthFilter.java          # Intercepts every request
    └── UserDetailsServiceImpl.java # Loads user for Spring Security
```

---

### Database Schema (2 tables, keep it simple)

**users**
```
id          BIGINT PK AUTO_INCREMENT
email       VARCHAR UNIQUE NOT NULL
password    VARCHAR NOT NULL          ← bcrypt hashed, never plain text
name        VARCHAR
created_at  TIMESTAMP
```

**transactions**
```
id          BIGINT PK AUTO_INCREMENT
user_id     BIGINT FK → users.id
type        ENUM('INCOME', 'EXPENSE')
category    VARCHAR                   ← e.g. "Food", "Rent", "Salary"
amount      DECIMAL(10,2)
description VARCHAR
date        DATE
created_at  TIMESTAMP
```

---

### API Endpoints

#### Auth (Public — no token needed)

| Method | Endpoint | Body | Response |
|---|---|---|---|
| POST | `/api/auth/register` | `{name, email, password}` | `{token, name, email}` |
| POST | `/api/auth/login` | `{email, password}` | `{token, name, email}` |

#### Transactions (Protected — JWT required)

| Method | Endpoint | Body/Params | Response |
|---|---|---|---|
| GET | `/api/transactions` | `?type=&category=&month=` | List of transactions |
| POST | `/api/transactions` | `{type, category, amount, description, date}` | Created transaction |
| PUT | `/api/transactions/{id}` | same as POST body | Updated transaction |
| DELETE | `/api/transactions/{id}` | — | 204 No Content |

#### Summary (Protected)

| Method | Endpoint | Response |
|---|---|---|
| GET | `/api/summary` | `{totalIncome, totalExpense, balance, byCategory: [...]}` |

> All protected endpoints read `user_id` from the JWT token — **never from the request body**. This is a key security principle.

---

### Security — What to Implement (and Why)

This is the most important section. Here are the 4 things your friend needs to implement:

---

**1. Password Hashing with BCrypt**

Never store plain text passwords. Spring Security has BCrypt built in.

```java
// In AuthService.java
passwordEncoder.encode(request.getPassword()) // when registering
passwordEncoder.matches(rawPassword, hashedPassword) // when logging in
```

*Why:* If the database is ever leaked, passwords stay safe.

---

**2. JWT (JSON Web Token) — Stateless Auth**

When a user logs in, generate a signed JWT and return it. The frontend stores this and sends it with every request in the `Authorization` header.

```
Authorization: Bearer <token>
```

The token contains: `userId`, `email`, `issuedAt`, `expiration`

Use the `io.jsonwebtoken` (jjwt) library. Set expiry to **24 hours** for simplicity.

```java
// JwtUtil.java — key methods to implement
String generateToken(UserDetails userDetails)
String extractEmail(String token)
boolean isTokenValid(String token, UserDetails userDetails)
```

*Why:* The server doesn't need to store sessions. Each request is self-contained.

---

**3. JWT Filter (The Core Security Piece)**

Every incoming request passes through `JwtAuthFilter` before hitting any controller.

```
Request → JwtAuthFilter → Check token → If valid, set authentication in context → Controller runs
```

```java
// JwtAuthFilter.java — logic flow
1. Read "Authorization" header
2. Extract token (strip "Bearer ")
3. Validate token with JwtUtil
4. If valid → set SecurityContextHolder authentication
5. Call filterChain.doFilter() to continue
```

*Why:* This is how Spring Security knows who is making the request without hitting the database every time (after the first user load).

---

**4. CORS Configuration**

Your frontend will run on `localhost:5173` (Vite), backend on `localhost:8080`. Without CORS config, the browser will block every request.

```java
// In SecurityConfig.java
.cors(cors -> cors.configurationSource(request -> {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:5173"));
    config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    return config;
}))
```

*Why:* Browser security policy blocks cross-origin requests by default. CORS tells the browser "this frontend is allowed to talk to me."

---

### What to Skip (For Now)

- ❌ Refresh tokens — single access token is fine for a project
- ❌ Role-based access (ADMIN/USER) — everyone is the same role
- ❌ Email verification
- ❌ Rate limiting
- ❌ PostgreSQL — H2 is fine, data resets on restart but that's okay

---

### Suggested 3-Day Plan for Your Friend

**Day 1 (~2.5 hrs):** Project setup on start.spring.io → create `User` entity + repository → implement register + login endpoints → BCrypt + JWT working → test with Postman

**Day 2 (~2.5 hrs):** Create `Transaction` entity → all 4 CRUD endpoints → wire JWT filter so protected routes work → test all endpoints with a token in Postman

**Day 3 (~1.5 hrs):** Build `/api/summary` endpoint → CORS config → clean up error responses (return proper HTTP status codes like 401, 404, 400) → share Postman collection with you

---

### Error Response Format (agree on this with your friend upfront)

You both need to agree on a consistent error shape so your frontend can handle it uniformly:

```json
{
  "status": 401,
  "message": "Invalid or expired token",
  "timestamp": "2026-03-03T10:00:00"
}
```

This lets you write one Axios interceptor on the frontend that handles all errors the same way.

---

That's everything your friend needs. The security section covers the 4 real principles — hashing, stateless auth, request interception, and CORS — which are exactly what gets asked in interviews too.