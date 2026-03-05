# FinTrack — Complete Project Documentation

# PART 1: FRONTEND DOCUMENTATION

## 1. Project Overview

FinTrack is a personal finance tracking web application. The frontend is a Single Page Application (SPA) built with React. It allows users to register, log in, and manage their income and expense transactions through a clean dashboard interface. The dashboard displays summary statistics and visual charts to help users understand their financial health at a glance.

This is not a multi-page website. The entire app loads once and React Router handles navigation between views without any page reloads.

---

## 2. Tech Stack — What Each Package Does

**React 19 + Vite**
React is the UI library. Vite is the build tool and dev server. You run `npm run dev` and Vite serves the app at `localhost:5173`. When you build for production, Vite bundles everything into optimized static files.

**React Router DOM v7**
Handles client-side routing. Defines which component renders at `/login`, `/register`, and `/dashboard`. Also handles redirecting unauthenticated users away from protected pages.

**Tailwind CSS v4**
Utility-first CSS framework. You style everything directly in JSX using class names like `className="flex items-center gap-4 p-6"`. There is no separate CSS file for components.

**shadcn/ui**
A component library where the source code lives directly in your project under `src/components/ui/`. It gives you pre-built, accessible components like Button, Card, Dialog, Form, Input, Select, Skeleton. You do not install these from npm — you add them via the shadcn CLI and they become part of your codebase.

**Recharts (via shadcn chart)**
A React charting library built on top of SVG. shadcn wraps it with a `ChartContainer` and config system that handles colors and tooltips automatically. You use it to render the PieChart and BarChart on the dashboard.

**Axios**
HTTP client for making API calls to the Spring Boot backend. Unlike the native `fetch`, Axios allows you to create an instance with a base URL and attach interceptors — functions that run automatically before every request or after every response.

**React Hook Form + Zod + @hookform/resolvers**
React Hook Form manages form state without re-rendering on every keystroke. Zod defines a validation schema (rules like "email must be valid", "password must be 8+ characters"). The resolver connects the two so form errors are automatically derived from the Zod schema.

**Sonner**
Toast notification library. After any action (adding a transaction, login failure, etc.) you call `toast.success("Done")` or `toast.error("Something went wrong")` from anywhere in the app.

**date-fns**
Utility library for formatting and manipulating dates. Used to display transaction dates in a readable format like "Mar 5, 2026" instead of raw ISO strings.

**lucide-react**
Icon library used internally by shadcn. You also use it directly for icons like edit pencil, trash, plus, etc.

**clsx + tailwind-merge + class-variance-authority**
Utility packages used by shadcn internally to conditionally combine Tailwind class names without conflicts. You will use `cn()` utility from `src/utils/cn.js` which combines these two.

---

## 3. Project Structure — Every File Explained

```
fintrack-ui/
├── public/
│   └── favicon.ico
│
├── src/
│   ├── main.jsx
│   ├── App.jsx
│   │
│   ├── config/
│   │   └── api.js
│   │
│   ├── constants/
│   │   ├── categories.js
│   │   └── queryKeys.js
│   │
│   ├── context/
│   │   └── AuthContext.jsx
│   │
│   ├── hooks/
│   │   ├── useAuth.js
│   │   ├── useTransactions.js
│   │   └── useSummary.js
│   │
│   ├── services/
│   │   ├── authService.js
│   │   └── transactionService.js
│   │
│   ├── utils/
│   │   ├── formatCurrency.js
│   │   ├── formatDate.js
│   │   └── cn.js
│   │
│   ├── pages/
│   │   ├── LoginPage.jsx
│   │   ├── RegisterPage.jsx
│   │   └── DashboardPage.jsx
│   │
│   ├── components/
│   │   ├── ui/                        ← shadcn owns this
│   │   │
│   │   ├── layout/
│   │   │   ├── AppLayout.jsx
│   │   │   ├── Sidebar.jsx
│   │   │   └── Topbar.jsx
│   │   │
│   │   ├── auth/
│   │   │   ├── LoginForm.jsx
│   │   │   ├── RegisterForm.jsx
│   │   │   └── ProtectedRoute.jsx
│   │   │
│   │   ├── dashboard/
│   │   │   ├── StatCard.jsx
│   │   │   ├── SpendingPieChart.jsx
│   │   │   └── MonthlyBarChart.jsx
│   │   │
│   │   └── transactions/
│   │       ├── TransactionList.jsx
│   │       ├── TransactionRow.jsx
│   │       ├── TransactionForm.jsx
│   │       ├── TransactionFilters.jsx
│   │       └── DeleteConfirmDialog.jsx
│   │
│   └── styles/
│       └── index.css
│
├── .env
├── .env.example
├── .gitignore
├── .prettierrc
├── eslint.config.js
├── index.html
├── vite.config.js
└── package.json
```

**File by file explanation:**

`src/main.jsx` — Entry point. Renders `<App />` into the DOM. Also renders the `<Toaster />` from Sonner here so it's available globally.

`src/App.jsx` — Sets up React Router with all routes. Wraps everything in `AuthProvider`. Uses `React.lazy()` to import page components so they are code-split into separate bundles.

`src/config/api.js` — Creates and exports a configured Axios instance. Sets `baseURL` from the environment variable. Attaches two interceptors: one that adds the JWT token to every request header, one that catches 401 responses and logs the user out.

`src/constants/categories.js` — Exports two arrays: `INCOME_CATEGORIES` and `EXPENSE_CATEGORIES`. Example: `["Salary", "Freelance", "Investment"]` and `["Food", "Rent", "Transport", "Shopping", "Health", "Entertainment", "Utilities"]`. Used in form dropdowns and chart configs.

`src/constants/queryKeys.js` — Exports string constants like `TRANSACTIONS = "transactions"` and `SUMMARY = "summary"`. Used as keys to avoid typos when referencing data in hooks.

`src/context/AuthContext.jsx` — Creates a React context that stores the logged-in user's information and token. On app load it reads the token from localStorage so the user stays logged in after refresh. Exposes `login()`, `logout()`, `user`, and `token` to all components.

`src/hooks/useAuth.js` — A simple hook that calls `useContext(AuthContext)` and returns the values. Components import `useAuth` instead of importing the context directly.

`src/hooks/useTransactions.js` — Fetches transaction data from the API. Manages `data`, `loading`, and `error` state internally. Exposes a `refetch` function so components can trigger a fresh fetch after mutations (add, edit, delete).

`src/hooks/useSummary.js` — Same pattern as `useTransactions` but fetches from `/api/summary`. Returns `totalIncome`, `totalExpense`, `balance`, and `byCategory`.

`src/services/authService.js` — Contains `login(email, password)` and `register(name, email, password)` functions. Each makes an API call using the Axios instance and returns the response data. No state management here — just API calls.

`src/services/transactionService.js` — Contains `getAll(filters)`, `create(data)`, `update(id, data)`, `deleteById(id)`. Same pattern — just API calls, no state.

`src/utils/formatCurrency.js` — Exports a function that takes a number and returns a formatted string like `₹85,000.00`. Uses the `Intl.NumberFormat` browser API.

`src/utils/formatDate.js` — Exports a function that takes a date string and returns a readable format using `date-fns`. Example: `"2026-03-05"` → `"Mar 5, 2026"`.

`src/utils/cn.js` — Exports the `cn()` utility function that combines `clsx` and `tailwind-merge`. Used for conditional class name composition. Auto-generated by shadcn init.

`src/pages/LoginPage.jsx` — A thin page-level wrapper. Centers the `LoginForm` on screen. No logic here.

`src/pages/RegisterPage.jsx` — Same as above for `RegisterForm`.

`src/pages/DashboardPage.jsx` — The main page. Composes `StatCard`s, charts, `TransactionFilters`, and `TransactionList` together. Holds the filter state and passes it down.

`src/components/layout/AppLayout.jsx` — The persistent shell for authenticated pages. Renders `Sidebar` on the left and `Topbar` at the top. The page content renders in the main area via React Router's `<Outlet />`.

`src/components/layout/Sidebar.jsx` — Navigation links using React Router's `NavLink`. Highlights the active route automatically.

`src/components/layout/Topbar.jsx` — Displays the logged-in user's name and a logout button that calls `AuthContext.logout()`.

`src/components/auth/LoginForm.jsx` — The actual login form built with shadcn Form + React Hook Form + Zod. On submit calls `authService.login()`, on success calls `AuthContext.login()` and navigates to `/dashboard`. On failure shows a Sonner error toast.

`src/components/auth/RegisterForm.jsx` — Same pattern as LoginForm for registration.

`src/components/auth/ProtectedRoute.jsx` — A wrapper component. If no token exists in AuthContext, redirects to `/login`. Otherwise renders the child component. Wraps the dashboard route in `App.jsx`.

`src/components/dashboard/StatCard.jsx` — Displays a single metric (Total Income, Total Expense, or Balance) using shadcn Card. Accepts `label`, `value`, and `type` props. Colors the value green for income, red for expense, neutral for balance.

`src/components/dashboard/SpendingPieChart.jsx` — Renders a Recharts PieChart inside shadcn ChartContainer. Data comes from `useSummary`'s `byCategory` array. Each slice represents a spending category.

`src/components/dashboard/MonthlyBarChart.jsx` — Renders a Recharts BarChart showing income vs expense bars for the last 6 months. Data comes from the summary endpoint.

`src/components/transactions/TransactionList.jsx` — Receives the transaction array, loading state, and error state as props. Renders a `TransactionRow` for each item. Shows shadcn Skeleton rows when loading. Shows an empty state message when no transactions exist.

`src/components/transactions/TransactionRow.jsx` — Renders one transaction as a table row. Shows category badge, description, date, amount, and Edit/Delete icon buttons.

`src/components/transactions/TransactionForm.jsx` — A shadcn Dialog containing a shadcn Form. Handles both Add and Edit modes — when an existing transaction is passed as a prop it pre-fills the form fields. On submit calls the appropriate service function and triggers `refetch`.

`src/components/transactions/TransactionFilters.jsx` — Contains a type dropdown (All/Income/Expense), a category dropdown, and a text search input. Calls a parent-provided `onFilterChange` callback whenever any filter changes.

`src/components/transactions/DeleteConfirmDialog.jsx` — A small shadcn Dialog with a warning message and two buttons: Cancel and Delete. On confirm calls `transactionService.deleteById()` and triggers `refetch`.

---

## 4. Authentication Flow — Step by Step

```
1. User fills LoginForm → submits
2. LoginForm calls authService.login(email, password)
3. authService sends POST /api/auth/login to backend
4. Backend validates credentials, returns { token, name, email }
5. authService returns that data to LoginForm
6. LoginForm calls AuthContext.login({ token, name, email })
7. AuthContext stores token in localStorage AND in React state
8. LoginForm navigates to /dashboard via useNavigate()

--- On every subsequent API call ---

9. Axios request interceptor reads token from localStorage
10. Adds "Authorization: Bearer <token>" header automatically
11. Backend validates token in JwtAuthFilter
12. If valid → request proceeds
13. If expired → backend returns 401
14. Axios response interceptor catches 401
15. Clears localStorage, calls AuthContext.logout()
16. Redirects to /login
```

---

## 5. Data Flow — How Transactions Work

```
DashboardPage
  → calls useTransactions() hook
      → hook calls transactionService.getAll()
          → Axios sends GET /api/transactions (with JWT header)
          → backend returns array of transactions
      → hook stores data in useState
  → passes data + loading + error to TransactionList
  → passes refetch to TransactionForm and DeleteConfirmDialog

When user adds a transaction:
  → TransactionForm submits
  → calls transactionService.create(formData)
  → on success: calls refetch()
  → useTransactions re-fetches from API
  → TransactionList re-renders with new data
  → Sonner shows success toast
```

---

## 6. Environment Variables

Create a `.env` file in the root:

```
VITE_API_BASE_URL=http://localhost:8080
```

Create a `.env.example` file (commit this to Git, never commit `.env`):

```
VITE_API_BASE_URL=http://localhost:8080
```

Access it in code as `import.meta.env.VITE_API_BASE_URL`. Vite only exposes variables prefixed with `VITE_`.

---

## 7. Routing Structure

```
/                    → redirects to /dashboard
/login               → LoginPage (public)
/register            → RegisterPage (public)
/dashboard           → DashboardPage (protected — needs JWT)
```

If a user visits `/dashboard` without a token, `ProtectedRoute` redirects them to `/login` immediately.

If a logged-in user visits `/login`, redirect them to `/dashboard` (no point showing login to someone already authenticated).

---

## 8. Key Patterns You Will Use Repeatedly

**shadcn Form field pattern (use this for every form field):**

```jsx
<FormField
  control={form.control}
  name="fieldName"
  render={({ field }) => (
    <FormItem>
      <FormLabel>Label</FormLabel>
      <FormControl>
        <Input {...field} />
      </FormControl>
      <FormMessage />
    </FormItem>
  )}
/>
```

**Sonner toast calls:**

```jsx
import { toast } from "sonner";
toast.success("Transaction added successfully");
toast.error("Failed to load data. Please try again.");
```

**Recharts chart config pattern:**

```jsx
const chartConfig = {
  income: { label: "Income", color: "hsl(var(--chart-1))" },
  expense: { label: "Expense", color: "hsl(var(--chart-2))" },
};
```

**Protected API call pattern (everything goes through the Axios instance, never raw fetch):**

```jsx
import api from "@/config/api";
const response = await api.get("/api/transactions");
```

---

## 9. What "Production Ready" Means in This Project

- Every async operation shows a loading state (shadcn Skeleton or disabled button)
- Every API error shows a Sonner toast — never a silent failure
- Forms show inline validation errors before submission
- The app does not crash on bad data — all edge cases show an empty state UI
- Token expiry is handled automatically by the Axios interceptor
- No hardcoded URLs anywhere — everything uses `VITE_API_BASE_URL`
- No `console.log` in final code
- Responsive down to 768px

---

---

# PART 2: BACKEND DOCUMENTATION

---

## 1. Project Overview

FinTrack's backend is a monolith REST API built with Spring Boot. It handles user registration, login, and all transaction CRUD operations. It is stateless — meaning the server stores no session data. Every request is authenticated by validating a JWT token sent in the request header.

The database is H2, an in-memory SQL database that requires zero setup. It seeds dummy data automatically on every startup using `schema.sql` and `data.sql` files.

---

## 2. Tech Stack — What Each Dependency Does

**Spring Boot 3.x**
The application framework. Handles HTTP request routing, dependency injection, and application lifecycle. You annotate a class with `@RestController` and Spring Boot automatically maps HTTP requests to your methods.

**Spring Security**
Security framework that intercepts every incoming HTTP request. You configure it to allow some endpoints publicly (login, register) and require a valid JWT for everything else. You also plug your custom JWT filter into its filter chain here.

**Spring Data JPA + Hibernate**
JPA is the interface for database operations. Hibernate is the implementation. You define entity classes (plain Java classes annotated with `@Entity`) and Spring generates the SQL queries automatically. You never write `SELECT * FROM transactions` — you call `transactionRepository.findByUserId(userId)`.

**H2 Database**
In-memory relational database. Lives entirely in RAM. Zero installation needed. Wiped clean on every restart. Has a browser console at `localhost:8080/h2-console` for inspecting data during development.

**Lombok**
Reduces boilerplate. Annotate a class with `@Data` and Lombok auto-generates getters, setters, equals, hashCode, and toString at compile time. Use `@Builder` for builder pattern. Use `@RequiredArgsConstructor` to inject dependencies without writing constructors manually.

**JJWT (io.jsonwebtoken)**
Library for generating and parsing JWT tokens. You use it in `JwtUtil.java` to create signed tokens on login and validate them on every subsequent request.

**Spring Validation**
Adds `@Valid`, `@NotBlank`, `@Email`, `@Size` annotations to DTO classes. When a request body fails validation, Spring automatically returns a 400 Bad Request with error details before your controller method even runs.

---

## 3. Project Structure — Every File Explained

```
src/main/java/com/fintrack/
│
├── config/
│   ├── SecurityConfig.java
│   └── JwtConfig.java
│
├── auth/
│   ├── AuthController.java
│   ├── AuthService.java
│   ├── AuthRequest.java
│   └── AuthResponse.java
│
├── user/
│   ├── User.java
│   └── UserRepository.java
│
├── transaction/
│   ├── Transaction.java
│   ├── TransactionRepository.java
│   ├── TransactionController.java
│   ├── TransactionService.java
│   ├── TransactionRequest.java
│   └── TransactionResponse.java
│
├── summary/
│   ├── SummaryController.java
│   └── SummaryService.java
│
└── security/
    ├── JwtUtil.java
    ├── JwtAuthFilter.java
    └── UserDetailsServiceImpl.java

src/main/resources/
├── application.properties
├── schema.sql
└── data.sql
```

**File by file explanation:**

`SecurityConfig.java` — The most important configuration file. Defines which endpoints are public and which require authentication. Disables CSRF (not needed for stateless JWT APIs). Configures CORS to allow requests from `http://localhost:5173`. Registers `JwtAuthFilter` into Spring Security's filter chain.

`JwtConfig.java` — Reads JWT secret key and expiration time from `application.properties` using `@Value`. Exposes them as beans so `JwtUtil` can use them.

`AuthController.java` — Has two endpoints: `POST /api/auth/register` and `POST /api/auth/login`. Delegates all logic to `AuthService`. Returns `AuthResponse` containing the JWT token and basic user info.

`AuthService.java` — Contains the actual register and login logic. On register: checks if email already exists, hashes the password with BCrypt, saves the user, generates a JWT, returns it. On login: loads user by email, verifies password with BCrypt, generates a JWT, returns it.

`AuthRequest.java` — DTO (Data Transfer Object). A plain class with `email` and `password` fields. Has `@NotBlank` and `@Email` validation annotations. This is what the request body deserializes into.

`AuthResponse.java` — DTO for the response. Contains `token`, `name`, and `email`. This is what gets serialized to JSON and sent back to the frontend.

`User.java` — JPA entity mapped to the `users` table. Fields: `id`, `email`, `password`, `name`, `createdAt`. Annotated with `@Entity`, `@Table(name = "users")`.

`UserRepository.java` — Extends `JpaRepository<User, Long>`. Spring generates all SQL automatically. You add one custom method: `Optional<User> findByEmail(String email)`.

`Transaction.java` — JPA entity mapped to the `transactions` table. Fields: `id`, `userId`, `type` (enum: INCOME/EXPENSE), `category`, `amount`, `description`, `date`, `createdAt`.

`TransactionRepository.java` — Extends `JpaRepository<Transaction, Long>`. Custom methods: `List<Transaction> findByUserId(Long userId)` and variations with filters for type and category.

`TransactionController.java` — Has four endpoints under `/api/transactions`: GET (list with optional filters), POST (create), PUT `/{id}` (update), DELETE `/{id}`. Extracts the authenticated user's ID from the security context — never from the request body.

`TransactionService.java` — Business logic for transactions. Validates that the transaction being edited or deleted actually belongs to the requesting user before performing the operation. Throws a 403 if someone tries to modify another user's data.

`TransactionRequest.java` — DTO for incoming transaction data: `type`, `category`, `amount`, `description`, `date`. All fields validated with annotations.

`TransactionResponse.java` — DTO for outgoing transaction data. Never expose the raw entity — always map to a response DTO before returning.

`SummaryController.java` — Single endpoint: `GET /api/summary`. Delegates to `SummaryService`.

`SummaryService.java` — Fetches all transactions for the user, computes `totalIncome`, `totalExpense`, `balance`, and a `byCategory` breakdown. Returns a structured summary object.

`JwtUtil.java` — Three key methods: `generateToken(UserDetails)` creates a signed JWT, `extractEmail(String token)` reads the email claim from a token, `isTokenValid(String token, UserDetails)` checks signature and expiry.

`JwtAuthFilter.java` — Extends `OncePerRequestFilter`. Runs once per request. Reads the `Authorization` header, strips `"Bearer "`, validates the token with `JwtUtil`, and if valid, sets the authentication object in `SecurityContextHolder` so Spring Security knows who the user is for this request.

`UserDetailsServiceImpl.java` — Implements Spring Security's `UserDetailsService` interface. Has one method: `loadUserByUsername(String email)` which fetches the user from the database. Spring Security calls this during authentication.

---

## 4. Database Schema

```sql
CREATE TABLE users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    type        VARCHAR(10) NOT NULL,
    category    VARCHAR(100) NOT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    date        DATE NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## 5. API Contract — Complete Reference

This is the single source of truth for the frontend-backend interface. Both sides must agree on this and never deviate.

### Base URL

```
http://localhost:8080
```

### Auth Header (all protected endpoints)

```
Authorization: Bearer <jwt_token>
```

### Error Response Shape (all errors, every endpoint)

```json
{
  "status": 401,
  "message": "Invalid or expired token",
  "timestamp": "2026-03-05T10:00:00"
}
```

---

### AUTH ENDPOINTS (Public)

**Register**

```
POST /api/auth/register

Request body:
{
  "name": "Alex Johnson",
  "email": "alex@example.com",
  "password": "password123"
}

Success response 200:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "name": "Alex Johnson",
  "email": "alex@example.com"
}

Error 409: Email already exists
Error 400: Validation failure (missing fields, invalid email)
```

**Login**

```
POST /api/auth/login

Request body:
{
  "email": "alex@example.com",
  "password": "password123"
}

Success response 200:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "name": "Alex Johnson",
  "email": "alex@example.com"
}

Error 401: Invalid credentials
Error 400: Validation failure
```

---

### TRANSACTION ENDPOINTS (Protected)

**Get all transactions**

```
GET /api/transactions
GET /api/transactions?type=EXPENSE
GET /api/transactions?category=Food
GET /api/transactions?type=EXPENSE&category=Food

Success response 200:
[
  {
    "id": 1,
    "type": "EXPENSE",
    "category": "Food",
    "amount": 4500.00,
    "description": "Groceries and dining",
    "date": "2026-03-01",
    "createdAt": "2026-03-01T10:00:00"
  }
]

Error 401: Missing or invalid token
```

**Create transaction**

```
POST /api/transactions

Request body:
{
  "type": "EXPENSE",
  "category": "Food",
  "amount": 1200.00,
  "description": "Restaurant dinner",
  "date": "2026-03-05"
}

Success response 201:
{
  "id": 29,
  "type": "EXPENSE",
  "category": "Food",
  "amount": 1200.00,
  "description": "Restaurant dinner",
  "date": "2026-03-05",
  "createdAt": "2026-03-05T18:30:00"
}

Error 400: Validation failure
Error 401: Unauthorized
```

**Update transaction**

```
PUT /api/transactions/{id}

Request body: same as POST

Success response 200: updated transaction object
Error 401: Unauthorized
Error 403: Transaction does not belong to this user
Error 404: Transaction not found
```

**Delete transaction**

```
DELETE /api/transactions/{id}

Success response 204: No content
Error 401: Unauthorized
Error 403: Transaction does not belong to this user
Error 404: Transaction not found
```

---

### SUMMARY ENDPOINT (Protected)

**Get financial summary**

```
GET /api/summary

Success response 200:
{
  "totalIncome": 510000.00,
  "totalExpense": 218600.00,
  "balance": 291400.00,
  "byCategory": [
    { "category": "Rent",          "total": 132000.00 },
    { "category": "Food",          "total": 28800.00  },
    { "category": "Shopping",      "total": 25300.00  },
    { "category": "Transport",     "total": 9200.00   },
    { "category": "Entertainment", "total": 6700.00   },
    { "category": "Health",        "total": 5300.00   },
    { "category": "Utilities",     "total": 18000.00  }
  ]
}

Error 401: Unauthorized
```

---

## 6. Security — Deep Explanation

### Why JWT and not sessions?

Traditional session-based auth stores a session ID in the server's memory and a cookie in the browser. This means the server must remember every logged-in user. JWT is stateless — the token itself contains the user's identity (email, userId), signed with a secret key. The server never stores anything. It just verifies the signature on each request. This scales better and is simpler for APIs consumed by a separate frontend.

### The complete security flow

```
1. User sends POST /api/auth/login with email + password
2. AuthService loads user from DB by email
3. BCrypt compares submitted password with stored hash
4. If match → JwtUtil generates a token signed with secret key
5. Token contains: email, userId, issuedAt, expiration (24hrs)
6. Token returned to frontend
7. Frontend stores token in localStorage
8. Frontend sends token in every request: Authorization: Bearer <token>

--- On every protected request ---

9.  JwtAuthFilter intercepts request before any controller runs
10. Reads Authorization header
11. Strips "Bearer " prefix to get raw token
12. JwtUtil validates: is signature valid? is it expired?
13. If valid: extracts email from token
14. Loads user from DB by email (UserDetailsServiceImpl)
15. Sets authentication in SecurityContextHolder
16. Request continues to controller
17. Controller reads userId from SecurityContextHolder — never from request body
```

### Why userId must come from the token, never from the request body

If you allowed the frontend to send `userId` in the request body, any user could forge a request claiming to be user ID 1 (the admin or another user) and read or modify their data. By reading userId only from the validated JWT, you guarantee the user is who they say they are.

### BCrypt explained

BCrypt is a one-way hashing algorithm. When you store `BCrypt("password123")` it produces a different hash every time due to a random salt. When verifying, you use `passwordEncoder.matches(rawPassword, storedHash)` — you never "decrypt" the hash, you just hash the input again and compare. Even if the database is leaked, attackers cannot reverse the hashes to plain text passwords.

---

## 7. application.properties — Complete Config

```properties
# App
spring.application.name=fintrack

# H2 Database
spring.datasource.url=jdbc:h2:mem:fintrackdb
spring.datasource.driver-class-name=org.H2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console (dev only)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.sql.init.mode=always

# JWT
jwt.secret=your-256-bit-secret-key-here-make-it-long-and-random
jwt.expiration=86400000

# Server
server.port=8080
```

`jwt.expiration=86400000` is 24 hours in milliseconds.

For `jwt.secret` generate a long random string — at least 32 characters. Never commit this to Git in a real project. For this learning project it is fine.

---

## 8. Dummy Data — Seed on Startup

Place these two files in `src/main/resources/`:

**schema.sql** — drops and recreates tables on every startup.

**data.sql** — inserts one demo user and 6 months of transactions.

Demo user credentials:

```
Email:    demo@fintrack.com
Password: password123
```

The password stored in `data.sql` is a pre-computed BCrypt hash of `password123`:

```
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
```

---

## 9. CORS — Why It Exists and What to Configure

CORS (Cross-Origin Resource Sharing) is a browser security feature. When your frontend at `localhost:5173` makes a request to your backend at `localhost:8080`, the browser blocks it by default because the ports differ (different origin). The backend must explicitly tell the browser "requests from this origin are allowed."

Configure in `SecurityConfig.java`:

```java
config.setAllowedOrigins(List.of("http://localhost:5173"));
config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
config.setAllowedHeaders(List.of("*"));
config.setAllowCredentials(true);
```

The `OPTIONS` method must be included — browsers send a preflight OPTIONS request before any cross-origin POST/PUT/DELETE to check if the server allows it.

---

## 10. Suggested 3-Day Implementation Plan

**Day 1 — Foundation + Auth (~2.5 hrs)**

- Generate project at start.spring.io with all dependencies
- Create `User` entity and `UserRepository`
- Implement `JwtUtil`, `UserDetailsServiceImpl`
- Implement `AuthController` + `AuthService` (register + login)
- Configure `SecurityConfig` minimally (just permit auth endpoints)
- Add `schema.sql` + `data.sql`
- Test register and login with Postman — confirm token is returned

**Day 2 — Transactions + Security Filter (~2.5 hrs)**

- Create `Transaction` entity and `TransactionRepository`
- Implement `JwtAuthFilter`
- Update `SecurityConfig` to require auth on `/api/transactions/**`
- Implement all four transaction endpoints
- Test all endpoints in Postman using the token from Day 1

**Day 3 — Summary + Polish (~1.5 hrs)**

- Implement `SummaryService` and `SummaryController`
- Configure CORS in `SecurityConfig`
- Add consistent error response format (create an exception handler with `@ControllerAdvice`)
- Return correct HTTP status codes everywhere (201 for create, 204 for delete, 403 for ownership violation)
- Share working Postman collection with the frontend developer

---

## 11. Postman Collection Structure (share this with frontend dev)

Create these requests in Postman so both of you can test independently:

```
FinTrack API
├── Auth
│   ├── Register          POST /api/auth/register
│   └── Login             POST /api/auth/login
├── Transactions
│   ├── Get All           GET  /api/transactions
│   ├── Get By Type       GET  /api/transactions?type=EXPENSE
│   ├── Create            POST /api/transactions
│   ├── Update            PUT  /api/transactions/1
│   └── Delete            DELETE /api/transactions/1
└── Summary
    └── Get Summary       GET  /api/summary
```

Set a Postman environment variable `token` — after running Login, copy the token value into it. Then set the Authorization header on all protected requests as `Bearer {{token}}`. This way you only update the token in one place.
