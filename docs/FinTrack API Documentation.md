Below is a **clean backend API documentation** you can give directly to a frontend developer.
This is written the way **internal backend teams usually document APIs**.

---

# FinTrack API Documentation

Backend for **FinTrack — Personal Finance Tracking Application**

**Base URL**

```
http://localhost:8080
```

**Authentication**

```
JWT Bearer Token
```

All protected endpoints require:

```
Authorization: Bearer <JWT_TOKEN>
```

---

# 1. Authentication APIs

Base path:

```
/api/auth
```

---

## 1.1 Register User

Creates a new user account.

**Endpoint**

```
POST /api/auth/register
```

### Request Body

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### Response (201 Created)

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "createdAt": "2026-03-13T10:15:30"
}
```

### Possible Errors

| Status | Reason                   |
| ------ | ------------------------ |
| 400    | Validation error         |
| 409    | Email already registered |

---

## 1.2 Login User

Authenticates a user and returns a **JWT token**.

**Endpoint**

```
POST /api/auth/login
```

### Request Body

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

### Response (200 OK)

```json
{
  "token": "JWT_TOKEN",
  "issuedAt": "2026-03-13T10:20:00"
}
```

### Notes

Frontend must store this token and send it in:

```
Authorization: Bearer <token>
```

---

# 2. Transactions API

All endpoints require **JWT authentication**.

Base path:

```
/api/transactions
```

---

## Transaction Object

```json
{
  "id": 1,
  "type": "EXPENSE",
  "category": "Food",
  "amount": 250.50,
  "description": "Dinner",
  "date": "2026-03-12"
}
```

### Transaction Types

```
INCOME
EXPENSE
```

---

# 2.1 Get Transactions

Returns paginated transactions with optional filtering.

**Endpoint**

```
GET /api/transactions
```

### Query Parameters

| Parameter | Type             | Description                |
| --------- | ---------------- | -------------------------- |
| type      | INCOME / EXPENSE | Filter by transaction type |
| category  | string           | Filter by category         |
| month     | integer          | Filter by month (1-12)     |
| page      | integer          | Page number                |
| size      | integer          | Page size                  |
| sort      | string           | Sorting                    |

### Example Request

```
GET /api/transactions?type=EXPENSE&category=Food&page=0&size=10&sort=date,desc
```

### Response

```json
{
  "content": [
    {
      "id": 1,
      "type": "EXPENSE",
      "category": "Food",
      "amount": 200,
      "description": "Lunch",
      "date": "2026-03-12"
    }
  ],
  "totalElements": 15,
  "totalPages": 2,
  "size": 10,
  "number": 0
}
```

---

# 2.2 Create Transaction

Creates a new transaction.

**Endpoint**

```
POST /api/transactions
```

### Request Body

```json
{
  "type": "EXPENSE",
  "category": "Food",
  "amount": 250,
  "description": "Dinner",
  "date": "2026-03-12"
}
```

### Response

```json
{
  "id": 10,
  "type": "EXPENSE",
  "category": "Food",
  "amount": 250,
  "description": "Dinner",
  "date": "2026-03-12"
}
```

---

# 2.3 Update Transaction

Updates an existing transaction.

**Endpoint**

```
PUT /api/transactions/{id}
```

### Example

```
PUT /api/transactions/10
```

### Request Body

```json
{
  "type": "EXPENSE",
  "category": "Food",
  "amount": 300,
  "description": "Dinner with friends",
  "date": "2026-03-12"
}
```

### Response

```json
{
  "id": 10,
  "type": "EXPENSE",
  "category": "Food",
  "amount": 300,
  "description": "Dinner with friends",
  "date": "2026-03-12"
}
```

---

# 2.4 Delete Transaction

Deletes a transaction.

**Endpoint**

```
DELETE /api/transactions/{id}
```

### Example

```
DELETE /api/transactions/10
```

### Response

```
204 No Content
```

---

# 3. Summary API

Provides **financial summary for the logged-in user**.

Base path:

```
/api/summary
```

---

## Get Financial Summary

**Endpoint**

```
GET /api/summary
```

### Response

```json
{
  "totalIncome": 5000,
  "totalExpense": 3200,
  "balance": 1800,
  "byCategory": [
    {
      "category": "Food",
      "amount": 600
    },
    {
      "category": "Rent",
      "amount": 1500
    },
    {
      "category": "Transport",
      "amount": 200
    }
  ]
}
```

---

# 4. Error Responses

All errors follow this format.

```json
{
  "timestamp": "2026-03-13T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid JWT token",
  "path": "/api/transactions"
}
```

---

# 5. Authentication Flow (Frontend)

### Step 1 — Register

```
POST /api/auth/register
```

---

### Step 2 — Login

```
POST /api/auth/login
```

Receive:

```
JWT Token
```

---

### Step 3 — Store Token

Example:

```
localStorage.setItem("token", token)
```

---

### Step 4 — Send Token in Requests

Example:

```
Authorization: Bearer <token>
```

---

# 6. Example Request with JWT

```
GET /api/transactions
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

# 7. Development Tools

Swagger UI available at:

```
/swagger-ui.html
```

API documentation:

```
/v3/api-docs
```

---

# 8. Tech Stack

Backend implemented using:

* Java 21
* Spring Boot
* Spring Security (JWT)
* Spring Data JPA
* MapStruct
* H2 / PostgreSQL
* OpenAPI (Swagger)

---

# Optional Improvements for Frontend Integration

Future APIs may include:

* Monthly analytics
* Category management
* Budget tracking
* Charts / spending trends

---

If you want, I can also create a **much more impressive "README.md style documentation" for GitHub** that makes this project look like a **production fintech backend** when recruiters view it.
