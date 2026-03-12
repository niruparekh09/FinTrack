# Pagination & Filtering — Easy Explanation

Let me break this down **piece by piece** so you understand every concept clearly.

---

## 🧠 First — What Problem Are We Solving?

Imagine your database has **10,000 transactions**.

```
Without Pagination:
GET /api/transactions → Returns ALL 10,000 rows 😱
→ Slow response
→ Browser crashes
→ Server memory explodes

With Pagination:
GET /api/transactions?page=0&size=10 → Returns only 10 rows ✅
GET /api/transactions?page=1&size=10 → Next 10 rows ✅
```

Think of it like **Google Search Results** — you see 10 results per page, not all millions.

---

## 📖 Now Let's Understand Each Piece

---

## 1️⃣ What is `Pageable`?

`Pageable` is a **Spring object** that holds three things.

```
Pageable contains:
├── page  → which page number (0, 1, 2...)
├── size  → how many items per page (10, 20, 50...)
└── sort  → how to order results (date DESC, amount ASC...)
```

When you write this in your controller:

```java
@GetMapping
public Page<TransactionResponse> getTransactions(Pageable pageable) {
    // ...
}
```

Spring **automatically reads** query parameters and creates the `Pageable` object.

```
GET /api/transactions?page=0&size=10&sort=date,desc

Spring creates:
pageable.getPageNumber() → 0
pageable.getPageSize()   → 10
pageable.getSort()       → date DESC
```

**You don't create `Pageable` manually.** Spring does it for you.

---

## 2️⃣ What is `Page<T>`?

`Page` is what the database **returns**. It contains.

```
Page<Transaction> contains:
├── content        → List of transactions (the actual data)
├── totalElements  → Total rows in database (e.g., 500)
├── totalPages     → Total pages (e.g., 50 if size=10)
├── number         → Current page number
├── size           → Page size
└── last/first     → Is this the last/first page?
```

Example JSON response:

```json
{
  "content": [
    {"id": 1, "category": "Food", "amount": 500},
    {"id": 2, "category": "Rent", "amount": 15000}
  ],
  "totalElements": 500,
  "totalPages": 50,
  "number": 0,
  "size": 10,
  "first": true,
  "last": false
}
```

Your **frontend** uses `totalPages` to show page buttons: `[1] [2] [3] ... [50]`

---

## 3️⃣ What is `Specification`? (The Filtering Part)

### The Problem

Imagine you need these different queries:

```sql
-- User wants all transactions
SELECT * FROM transactions WHERE user_id = 1

-- User wants only EXPENSES
SELECT * FROM transactions WHERE user_id = 1 AND type = 'EXPENSE'

-- User wants EXPENSES in Food category
SELECT * FROM transactions WHERE user_id = 1 AND type = 'EXPENSE' AND category = 'Food'

-- User wants transactions in May
SELECT * FROM transactions WHERE user_id = 1 AND date BETWEEN '2025-05-01' AND '2025-06-01'
```

**Without Specification**, you'd need to write **separate repository methods** for every combination.

```java
// This is BAD — too many methods! ❌
findByUserId(Long userId);
findByUserIdAndType(Long userId, TransactionType type);
findByUserIdAndTypeAndCategory(Long userId, TransactionType type, String category);
findByUserIdAndMonth(Long userId, Integer month);
findByUserIdAndTypeAndMonth(Long userId, TransactionType type, Integer month);
// ... 20 more combinations 😩
```

### The Solution — Specification

`Specification` **dynamically builds** the WHERE clause.

```
Think of it as building blocks:

Always add:       WHERE user_id = ?
If type given:    AND type = ?
If category given: AND category LIKE ?
If month given:   AND date BETWEEN ? AND ?
```

Only the **relevant conditions** are added.

---

## 4️⃣ Understanding the Specification Code — Line by Line

```java
public class TransactionSpecification {

    public static Specification<Transaction> filterBy(
            Long userId,
            TransactionType type,      // can be null
            String category,           // can be null
            Integer month              // can be null
    ) {

        return (root, query, cb) -> {
            // root = the Transaction table
            // query = the SELECT query being built
            // cb = CriteriaBuilder (helps create conditions)
```

### What are `root`, `query`, `cb`?

Think of it like writing SQL manually.

```
root  → represents the TABLE (Transaction)
        root.get("category") → means Transaction.category column

cb    → CriteriaBuilder — a TOOL to create conditions
        cb.equal(...)    → creates "column = value"
        cb.like(...)     → creates "column LIKE value"
        cb.between(...)  → creates "column BETWEEN x AND y"

query → the full SELECT query (rarely used directly)
```

### Building Conditions Step by Step

```java
List<Predicate> predicates = new ArrayList<>();
```

`Predicate` = one condition in the WHERE clause. We **collect** all conditions in a list.

```java
// ALWAYS added — security! User can only see their own data
predicates.add(cb.equal(root.get("userId"), userId));
// SQL: WHERE user_id = 5
```

```java
// Only added if user sent ?type=EXPENSE
if (type != null) {
    predicates.add(cb.equal(root.get("type"), type));
    // SQL: AND type = 'EXPENSE'
}
```

```java
// Only added if user sent ?category=Food
if (category != null && !category.isBlank()) {
    predicates.add(
        cb.like(
            cb.lower(root.get("category")),    // convert column to lowercase
            "%" + category.toLowerCase() + "%" // partial match
        )
    );
    // SQL: AND LOWER(category) LIKE '%food%'
    // This matches: "Food", "food", "Fast Food", "FOOD items"
}
```

```java
// Only added if user sent ?month=5
if (month != null) {
    LocalDate start = LocalDate.of(LocalDate.now().getYear(), month, 1);
    // start = 2025-05-01

    LocalDate end = start.plusMonths(1);
    // end = 2025-06-01

    predicates.add(cb.between(root.get("date"), start, end));
    // SQL: AND date BETWEEN '2025-05-01' AND '2025-06-01'
}
```

### Finally — Combine All Conditions

```java
return cb.and(predicates.toArray(new Predicate[0]));
// Joins all predicates with AND
```

Visual example:

```
predicates = [
    user_id = 5,
    type = 'EXPENSE',
    category LIKE '%food%'
]

Result: WHERE user_id = 5 AND type = 'EXPENSE' AND category LIKE '%food%'
```

---

## 5️⃣ How Everything Connects — Full Flow

```
Client Request:
GET /api/transactions?type=EXPENSE&category=Food&page=0&size=10&sort=date,desc
                 │
                 ▼
┌──────────────────────────────────────────┐
│           CONTROLLER                     │
│                                          │
│  type = EXPENSE                          │
│  category = "Food"                       │
│  month = null                            │
│  pageable = {page:0, size:10, sort:date} │
│                                          │
│  calls service.getTransactions(...)      │
└──────────────────┬───────────────────────┘
                   ▼
┌──────────────────────────────────────────┐
│           SERVICE                        │
│                                          │
│  userId = SecurityUtils.getCurrentUserId │
│                  → 5                     │
│                                          │
│  spec = TransactionSpecification         │
│         .filterBy(5, EXPENSE, "Food",    │
│                                  null)   │
│                                          │
│  transactions = repo.findAll(spec,       │
│                              pageable)   │
│                                          │
│  return transactions.map(mapper::toDTO)  │
└──────────────────┬───────────────────────┘
                   ▼
┌──────────────────────────────────────────┐
│         SPECIFICATION                    │
│                                          │
│  Builds dynamically:                     │
│  WHERE user_id = 5                       │
│    AND type = 'EXPENSE'                  │
│    AND LOWER(category) LIKE '%food%'     │
│                                          │
│  (month was null, so NOT added)          │
└──────────────────┬───────────────────────┘
                   ▼
┌──────────────────────────────────────────┐
│         DATABASE QUERY                   │
│                                          │
│  SELECT * FROM transactions              │
│  WHERE user_id = 5                       │
│    AND type = 'EXPENSE'                  │
│    AND LOWER(category) LIKE '%food%'     │
│  ORDER BY date DESC                      │
│  LIMIT 10 OFFSET 0                       │
└──────────────────┬───────────────────────┘
                   ▼
┌──────────────────────────────────────────┐
│         RESPONSE                         │
│                                          │
│  {                                       │
│    "content": [{...}, {...}],            │
│    "totalElements": 45,                  │
│    "totalPages": 5,                      │
│    "number": 0                           │
│  }                                       │
└──────────────────────────────────────────┘
```

---

## 6️⃣ `Page.map()` — Converting Entity to DTO

```java
Page<Transaction> transactions = repo.findAll(spec, pageable);
// This contains Entity objects (with all database fields)

Page<TransactionResponse> response = transactions.map(mapper::toTransactionResponse);
// This converts EACH entity to a DTO
// But keeps all pagination info (totalPages, totalElements, etc.)
```

What `.map()` does visually:

```
BEFORE (Page<Transaction>):
content: [Transaction{id=1, userId=5, category="Food", amount=500, ...}]
totalPages: 5

AFTER (Page<TransactionResponse>):
content: [TransactionResponse{category="Food", amount=500}]  ← no userId exposed
totalPages: 5  ← pagination info preserved!
```

---

## 7️⃣ `@PageableDefault` — Setting Default Values

```java
@PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC)
Pageable pageable
```

This means:

```
If client sends:  GET /api/transactions
(no page params)

Defaults applied:
  page = 0
  size = 20        ← not 10 or 100
  sort = date DESC ← newest first
```

This prevents someone from sending `?size=1000000` and crashing your server.

---

## 8️⃣ Quick Summary Card

```
┌───────────────────────────────────────────────────┐
│              PAGINATION + FILTERING               │
├───────────────────────────────────────────────────┤
│                                                   │
│  Pageable          → Holds page, size, sort       │
│  Page<T>           → Result with pagination info  │
│  Specification     → Dynamic WHERE clause builder │
│  Predicate         → One condition (type = ?)     │
│  CriteriaBuilder   → Tool to create conditions    │
│  root              → Represents the table         │
│  Page.map()        → Entity → DTO conversion      │
│  @PageableDefault  → Default pagination settings  │
│                                                   │
│  Repository needs:                                │
│    extends JpaSpecificationExecutor<T>            │
│                                                   │
│  Query params Spring handles automatically:       │
│    ?page=0&size=10&sort=date,desc                 │
│                                                   │
└───────────────────────────────────────────────────┘
```

This entire pattern is what **real fintech companies use** — the same concept, just at larger scale.