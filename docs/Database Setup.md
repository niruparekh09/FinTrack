## Seeding H2 Database on Startup

Spring Boot has a built-in mechanism for this. Here's exactly what your friend needs to do.

---

### How It Works

Spring Boot automatically picks up two files from `src/main/resources/` on startup:

- `schema.sql` — creates the tables
- `data.sql` — inserts the dummy data

That's it. No extra code needed.

---

### Step 1 — `application.properties` config

Add these lines so Spring Boot knows to run the SQL files and doesn't conflict with JPA auto table creation:

```properties
# H2 Config
spring.datasource.url=jdbc:h2:mem:fintrackdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console (access at localhost:8080/h2-console during dev)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Tell JPA not to auto-create tables — we do it via schema.sql
spring.jpa.hibernate.ddl-auto=none

# Tell Spring Boot to always run schema.sql + data.sql
spring.sql.init.mode=always
```

---

### Step 2 — `schema.sql`

```sql
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS users;

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
    type        VARCHAR(10) NOT NULL,       -- 'INCOME' or 'EXPENSE'
    category    VARCHAR(100) NOT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    date        DATE NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

The `DROP TABLE IF EXISTS` at the top ensures every restart gets a clean slate — since H2 is in-memory anyway, this is just good practice.

---

### Step 3 — `data.sql`

**Important note on the password:** The dummy user's password is `password123`. The value below is its BCrypt hash. Your friend cannot just type plain text here — it must be pre-hashed because Spring Security will use `passwordEncoder.matches()` to verify it.

This is the BCrypt hash of `password123`:
```
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
```

```sql
-- Dummy user
-- Email: demo@fintrack.com | Password: password123
INSERT INTO users (email, password, name, created_at) VALUES (
    'demo@fintrack.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Alex Johnson',
    CURRENT_TIMESTAMP
);

-- Transactions for user_id = 1 (last 6 months of data for charts)

-- INCOME transactions
INSERT INTO transactions (user_id, type, category, amount, description, date) VALUES
(1, 'INCOME', 'Salary',     85000.00, 'Monthly salary - October',   '2025-10-01'),
(1, 'INCOME', 'Freelance',  12000.00, 'Website project for client', '2025-10-15'),
(1, 'INCOME', 'Salary',     85000.00, 'Monthly salary - November',  '2025-11-01'),
(1, 'INCOME', 'Freelance',   8500.00, 'Logo design project',        '2025-11-20'),
(1, 'INCOME', 'Salary',     85000.00, 'Monthly salary - December',  '2025-12-01'),
(1, 'INCOME', 'Investment',  5000.00, 'Dividend payout',            '2025-12-10'),
(1, 'INCOME', 'Salary',     85000.00, 'Monthly salary - January',   '2026-01-01'),
(1, 'INCOME', 'Freelance',  15000.00, 'Mobile app UI project',      '2026-01-18'),
(1, 'INCOME', 'Salary',     85000.00, 'Monthly salary - February',  '2026-02-01'),
(1, 'INCOME', 'Salary',     85000.00, 'Monthly salary - March',     '2026-03-01');

-- EXPENSE transactions
INSERT INTO transactions (user_id, type, category, amount, description, date) VALUES
(1, 'EXPENSE', 'Rent',          22000.00, 'Monthly rent',                '2025-10-05'),
(1, 'EXPENSE', 'Food',           4500.00, 'Groceries and dining',        '2025-10-12'),
(1, 'EXPENSE', 'Transport',      1800.00, 'Fuel and Uber',               '2025-10-20'),
(1, 'EXPENSE', 'Entertainment',  2200.00, 'Netflix, movies, weekend',    '2025-10-25'),
(1, 'EXPENSE', 'Utilities',      3100.00, 'Electricity and internet',    '2025-10-28'),
(1, 'EXPENSE', 'Rent',          22000.00, 'Monthly rent',                '2025-11-05'),
(1, 'EXPENSE', 'Food',           5200.00, 'Groceries and dining out',    '2025-11-14'),
(1, 'EXPENSE', 'Shopping',       7800.00, 'Winter clothing',             '2025-11-19'),
(1, 'EXPENSE', 'Transport',      1600.00, 'Monthly commute',             '2025-11-22'),
(1, 'EXPENSE', 'Utilities',      2900.00, 'Electricity and internet',    '2025-11-28'),
(1, 'EXPENSE', 'Rent',          22000.00, 'Monthly rent',                '2025-12-05'),
(1, 'EXPENSE', 'Food',           6800.00, 'Groceries + holiday dining',  '2025-12-15'),
(1, 'EXPENSE', 'Shopping',      12000.00, 'Christmas/holiday gifts',     '2025-12-20'),
(1, 'EXPENSE', 'Entertainment',  4500.00, 'Holiday outings',             '2025-12-24'),
(1, 'EXPENSE', 'Utilities',      3200.00, 'Electricity and internet',    '2025-12-28'),
(1, 'EXPENSE', 'Rent',          22000.00, 'Monthly rent',                '2026-01-05'),
(1, 'EXPENSE', 'Food',           4800.00, 'Groceries and dining',        '2026-01-10'),
(1, 'EXPENSE', 'Health',         3500.00, 'Gym membership + checkup',    '2026-01-15'),
(1, 'EXPENSE', 'Transport',      2100.00, 'Fuel and cab rides',          '2026-01-21'),
(1, 'EXPENSE', 'Utilities',      2800.00, 'Electricity and internet',    '2026-01-28'),
(1, 'EXPENSE', 'Rent',          22000.00, 'Monthly rent',                '2026-02-05'),
(1, 'EXPENSE', 'Food',           4300.00, 'Groceries and dining',        '2026-02-12'),
(1, 'EXPENSE', 'Shopping',       5500.00, 'Electronics - keyboard',      '2026-02-16'),
(1, 'EXPENSE', 'Transport',      1700.00, 'Fuel and Uber',               '2026-02-20'),
(1, 'EXPENSE', 'Utilities',      3000.00, 'Electricity and internet',    '2026-02-26'),
(1, 'EXPENSE', 'Rent',          22000.00, 'Monthly rent',                '2026-03-05'),
(1, 'EXPENSE', 'Food',           3200.00, 'Groceries',                   '2026-03-01'),
(1, 'EXPENSE', 'Health',         1800.00, 'Pharmacy',                    '2026-03-02');
```

---

### File placement summary

```
src/main/resources/
├── application.properties    ← add the config from Step 1
├── schema.sql                ← Step 2
└── data.sql                  ← Step 3
```

---

### What your friend gets out of the box

When the backend starts, there will be a ready-to-use account:

| Field | Value |
|---|---|
| Email | `demo@fintrack.com` |
| Password | `password123` |
| Data | 6 months of income + expense transactions |

The data is intentionally varied across categories — Food, Rent, Salary, Freelance, Shopping, Health, Transport — so your pie chart and bar chart will actually look meaningful from day one instead of showing a single slice.