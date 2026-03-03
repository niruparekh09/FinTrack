-- Dummy user
-- Email: demo@fintrack.com | Password: password123
INSERT INTO users (email, password, name, created_at)
VALUES ('demo@fintrack.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'Alex Johnson',
        CURRENT_TIMESTAMP);

-- Transactions for user_id = 1 (last 6 months of data for charts)

-- INCOME transactions
INSERT INTO transactions (user_id, type, category, amount, description, date)
VALUES (1, 'INCOME', 'Salary', 85000.00, 'Monthly salary - October', '2025-10-01'),
       (1, 'INCOME', 'Freelance', 12000.00, 'Website project for client', '2025-10-15'),
       (1, 'INCOME', 'Salary', 85000.00, 'Monthly salary - November', '2025-11-01'),
       (1, 'INCOME', 'Freelance', 8500.00, 'Logo design project', '2025-11-20'),
       (1, 'INCOME', 'Salary', 85000.00, 'Monthly salary - December', '2025-12-01'),
       (1, 'INCOME', 'Investment', 5000.00, 'Dividend payout', '2025-12-10'),
       (1, 'INCOME', 'Salary', 85000.00, 'Monthly salary - January', '2026-01-01'),
       (1, 'INCOME', 'Freelance', 15000.00, 'Mobile app UI project', '2026-01-18'),
       (1, 'INCOME', 'Salary', 85000.00, 'Monthly salary - February', '2026-02-01'),
       (1, 'INCOME', 'Salary', 85000.00, 'Monthly salary - March', '2026-03-01');

-- EXPENSE transactions
INSERT INTO transactions (user_id, type, category, amount, description, date)
VALUES (1, 'EXPENSE', 'Rent', 22000.00, 'Monthly rent', '2025-10-05'),
       (1, 'EXPENSE', 'Food', 4500.00, 'Groceries and dining', '2025-10-12'),
       (1, 'EXPENSE', 'Transport', 1800.00, 'Fuel and Uber', '2025-10-20'),
       (1, 'EXPENSE', 'Entertainment', 2200.00, 'Netflix, movies, weekend', '2025-10-25'),
       (1, 'EXPENSE', 'Utilities', 3100.00, 'Electricity and internet', '2025-10-28'),
       (1, 'EXPENSE', 'Rent', 22000.00, 'Monthly rent', '2025-11-05'),
       (1, 'EXPENSE', 'Food', 5200.00, 'Groceries and dining out', '2025-11-14'),
       (1, 'EXPENSE', 'Shopping', 7800.00, 'Winter clothing', '2025-11-19'),
       (1, 'EXPENSE', 'Transport', 1600.00, 'Monthly commute', '2025-11-22'),
       (1, 'EXPENSE', 'Utilities', 2900.00, 'Electricity and internet', '2025-11-28'),
       (1, 'EXPENSE', 'Rent', 22000.00, 'Monthly rent', '2025-12-05'),
       (1, 'EXPENSE', 'Food', 6800.00, 'Groceries + holiday dining', '2025-12-15'),
       (1, 'EXPENSE', 'Shopping', 12000.00, 'Christmas/holiday gifts', '2025-12-20'),
       (1, 'EXPENSE', 'Entertainment', 4500.00, 'Holiday outings', '2025-12-24'),
       (1, 'EXPENSE', 'Utilities', 3200.00, 'Electricity and internet', '2025-12-28'),
       (1, 'EXPENSE', 'Rent', 22000.00, 'Monthly rent', '2026-01-05'),
       (1, 'EXPENSE', 'Food', 4800.00, 'Groceries and dining', '2026-01-10'),
       (1, 'EXPENSE', 'Health', 3500.00, 'Gym membership + checkup', '2026-01-15'),
       (1, 'EXPENSE', 'Transport', 2100.00, 'Fuel and cab rides', '2026-01-21'),
       (1, 'EXPENSE', 'Utilities', 2800.00, 'Electricity and internet', '2026-01-28'),
       (1, 'EXPENSE', 'Rent', 22000.00, 'Monthly rent', '2026-02-05'),
       (1, 'EXPENSE', 'Food', 4300.00, 'Groceries and dining', '2026-02-12'),
       (1, 'EXPENSE', 'Shopping', 5500.00, 'Electronics - keyboard', '2026-02-16'),
       (1, 'EXPENSE', 'Transport', 1700.00, 'Fuel and Uber', '2026-02-20'),
       (1, 'EXPENSE', 'Utilities', 3000.00, 'Electricity and internet', '2026-02-26'),
       (1, 'EXPENSE', 'Rent', 22000.00, 'Monthly rent', '2026-03-05'),
       (1, 'EXPENSE', 'Food', 3200.00, 'Groceries', '2026-03-01'),
       (1, 'EXPENSE', 'Health', 1800.00, 'Pharmacy', '2026-03-02');
