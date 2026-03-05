Perfect setup. Here's what changes in the plan with your new stack.

---

## What Changed Architecturally

| Old Plan | New Plan |
|---|---|
| Chart.js + react-chartjs-2 | Recharts via shadcn `chart` component |
| react-hot-toast | Sonner (`<Toaster>` from shadcn) |
| Hand-rolled UI components | shadcn `Button`, `Card`, `Dialog`, `Form` |
| Custom skeleton divs | shadcn `Skeleton` component |

---

## Updated File Structure Changes

The `src/components/ui/` folder is now **entirely owned by shadcn**. Don't touch anything in there. The components you'll actually build change slightly:

**Remove these from your build plan** (shadcn handles them):
- Custom `Button.jsx` — gone
- Custom `Modal.jsx` — gone, use shadcn `Dialog`
- Custom skeleton div — gone, use shadcn `Skeleton`

**Your `components/` folder now looks like this:**

```
src/components/
│
├── ui/                          ← shadcn owns this, never edit manually
│   ├── button.jsx
│   ├── card.jsx
│   ├── dialog.jsx
│   ├── form.jsx
│   ├── input.jsx
│   ├── label.jsx
│   ├── select.jsx
│   ├── skeleton.jsx
│   ├── sonner.jsx
│   └── chart.jsx               ← recharts wrapper from shadcn
│
├── layout/
│   ├── AppLayout.jsx
│   ├── Sidebar.jsx
│   └── Topbar.jsx
│
├── auth/
│   ├── LoginForm.jsx
│   ├── RegisterForm.jsx
│   └── ProtectedRoute.jsx
│
├── dashboard/
│   ├── StatCard.jsx             ← uses shadcn Card internally
│   ├── SpendingPieChart.jsx     ← uses shadcn chart (Recharts PieChart)
│   └── MonthlyBarChart.jsx      ← uses shadcn chart (Recharts BarChart)
│
└── transactions/
    ├── TransactionList.jsx
    ├── TransactionRow.jsx
    ├── TransactionForm.jsx      ← uses shadcn Form + Dialog
    ├── TransactionFilters.jsx
    └── DeleteConfirmDialog.jsx  ← uses shadcn Dialog
```

---

## Updated 10-Day Plan

### Day 1 — No change
Project setup, folder skeleton, `.env`, push to GitHub. Same as before.

---

### Day 2 — No change
Axios instance, interceptors, `authService.js`. Stack doesn't affect this at all.

---

### Day 3 — No change
`AuthContext`, `ProtectedRoute`, React Router setup, `React.lazy()`. Same as before.

---

### Day 4 — Updated

**LoginForm + RegisterForm with shadcn Form**

This day gets more structured now. shadcn's `Form` component is a wrapper around `react-hook-form` that gives you built-in error message rendering and accessible labels. The pattern looks like this:

```jsx
// Every form field follows this exact structure
<FormField
  control={form.control}
  name="email"
  render={({ field }) => (
    <FormItem>
      <FormLabel>Email</FormLabel>
      <FormControl>
        <Input placeholder="you@example.com" {...field} />
      </FormControl>
      <FormMessage />   {/* zod errors render here automatically */}
    </FormItem>
  )}
/>
```

Also on Day 4 — set up Sonner globally in `main.jsx` or `App.jsx`:

```jsx
import { Toaster } from "@/components/ui/sonner"

// Inside your App return
<Toaster position="top-right" richColors />
```

Then anywhere in your app you just call:
```jsx
import { toast } from "sonner"

toast.success("Transaction added!")
toast.error("Invalid credentials")
```

---

### Day 5 — Slightly updated

**AppLayout + Sidebar using shadcn Card where appropriate**

Use shadcn `Card` for the sidebar container and any content panels. Nothing major changes here — it's mostly layout work with Tailwind, same as before.

---

### Day 6 — No change
`transactionService.js`, `useTransactions.js`, `useSummary.js`. Pure logic, no UI.

---

### Day 7 — Updated

**Transaction CRUD with shadcn Dialog + Form**

`TransactionForm.jsx` now uses shadcn `Dialog` + shadcn `Form` together. The pattern:

```jsx
<Dialog open={open} onOpenChange={setOpen}>
  <DialogTrigger asChild>
    <Button>Add Transaction</Button>
  </DialogTrigger>
  <DialogContent>
    <DialogHeader>
      <DialogTitle>Add Transaction</DialogTitle>
    </DialogHeader>
    {/* shadcn Form goes here */}
  </DialogContent>
</Dialog>
```

`DeleteConfirmDialog.jsx` uses the same `Dialog` but with just two buttons — Cancel and Confirm. No form needed.

On success of any mutation, call `toast.success()` from Sonner. On error, call `toast.error()`. This replaces all the manual error state you would have managed before.

---

### Day 8 — No change
Filters + search. Client-side filtering logic. Stack doesn't affect this.

---

### Day 9 — Updated (biggest change)

**Charts with shadcn chart component + Recharts**

shadcn's chart component is a thin, opinionated wrapper around Recharts that handles theming and tooltips for you. The key thing to understand is the **config object pattern** — you define colors and labels once, and the chart + tooltip use it automatically:

```jsx
// SpendingPieChart.jsx
const chartConfig = {
  Food:          { label: "Food",          color: "hsl(var(--chart-1))" },
  Rent:          { label: "Rent",          color: "hsl(var(--chart-2))" },
  Transport:     { label: "Transport",     color: "hsl(var(--chart-3))" },
  Entertainment: { label: "Entertainment", color: "hsl(var(--chart-4))" },
  Shopping:      { label: "Shopping",      color: "hsl(var(--chart-5))" },
}

// Then wrap your Recharts component in shadcn's ChartContainer
<ChartContainer config={chartConfig} className="h-[300px]">
  <PieChart>
    <Pie data={data} dataKey="amount" nameKey="category" />
    <ChartTooltip content={<ChartTooltipContent />} />
  </PieChart>
</ChartContainer>
```

For `MonthlyBarChart.jsx` it's the same pattern but with Recharts `BarChart` and `Bar`.

`StatCard.jsx` uses shadcn `Card`, `CardHeader`, `CardContent`, `CardTitle` — straightforward.

---

### Day 10 — No change
Polish, error handling, README, production readiness. Same as before.

---

## One Thing to Remember About shadcn

shadcn components live in *your* codebase, not in `node_modules`. When you run `npx shadcn@latest add button`, it copies the source into `src/components/ui/button.jsx`. This means:

- ✅ You can read the code and understand exactly what it does
- ✅ You can customize it if needed
- ❌ Don't edit it casually — if you re-run the add command it will overwrite your changes

That's it. The 10-day structure is identical, Days 4, 7, and 9 just use cleaner, more modern patterns. Ready for Day 1?







2nd ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Here are the resume points for both of you, following the exact same format.

---

## For You (Frontend Dev — 4 points)

```latex
{\textbf{FinTrack} $|$ \emph{React, Vite, Tailwind CSS, Recharts, shadcn/ui, Axios}}{2026}
\resumeItemListStart
  \resumeItem{Architected a responsive personal finance dashboard in React with lazy-loaded routes and code splitting via React.lazy(), improving initial load performance}
  \resumeItem{Engineered a centralized Axios instance with JWT request interceptors and 401 response handling, enabling secure and consistent communication with a Spring Boot REST API}
  \resumeItem{Built complex financial data visualizations using Recharts, rendering category-wise spending breakdowns and 6-month income vs expense trends to support data-driven decisions}
  \resumeItem{Implemented type-safe authentication and transaction forms using React Hook Form and Zod schema validation, integrated with shadcn/ui Dialog and Form components}
\resumeItemListEnd
```

---

## For Your Friend (Fullstack Dev — 2 backend, 2 frontend)

```latex
{\textbf{FinTrack} $|$ \emph{Spring Boot, Spring Security, JWT, H2, React, Tailwind CSS}}{2026}
\resumeItemListStart
  \resumeItem{Developed a secure monolith REST API in Spring Boot with JWT-based stateless authentication, BCrypt password hashing, and a custom JWT filter integrated into the Spring Security filter chain}
  \resumeItem{Designed and implemented protected transaction and summary endpoints with user-scoped data access, ensuring user\_id is always derived from the JWT token and never trusted from the request body}
  \resumeItem{Built the frontend dashboard UI with React and Tailwind CSS, consuming secured REST endpoints via Axios with authorization headers managed through context-level auth state}
  \resumeItem{Implemented CORS configuration to enable cross-origin communication between the Vite dev server and Spring Boot backend, with environment-aware allowed origins}
\resumeItemListEnd
```