# FamilyLedger - Technical Architecture & Feature Audit Manual

This document provides a detailed specification of all modules, components, data flows, and state management in **FamilyLedger** to facilitate code audits, maintenance, and future expansion.

---

## 1. Domain Models (`com.example.shared.models`)

| Model Class | Description | Key Fields |
|---|---|---|
| `Member` | Represents a family member in the household. | `id`, `name`, `role` ("Husband" / "Wife"), `avatarUrl` |
| `Wallet` | Financial account holding balances. | `id`, `name`, `type` ("Cash", "Bank", "E-Wallet"), `balance`, `memberId` |
| `Category` | Spending/Income classification. | `id`, `name`, `type` ("Expense" / "Income"), `iconRes`, `color` |
| `Transaction` | Financial record entry. | `id`, `walletId`, `memberId`, `categoryId`, `amount`, `note`, `timestamp` |
| `FinancialGoal` | Savings target tracker. | `id`, `title`, `targetAmount`, `currentAmount`, `category`, `emoji` |
| `RecurringBill` | Subscriptions or monthly bills. | `id`, `title`, `amount`, `dueDate`, `isPaid`, `iconEmoji` |
| `TransferNotification` | Inter-spousal transfer notification state. | `id`, `senderId`, `senderRole`, `recipientId`, `recipientRole`, `amount`, `selectedEmoji`, `status` |

---

## 2. Core Subsystems (`com.example.core`)

### 2.1 Storage & Repository (`core/storage/`)
- **`AppDatabase.kt`**: Room database definition providing DAOs for Transactions, Wallets, Categories, Goals, and Recurring Bills.
- **`FamilyRepository.kt`**: Single source of truth interface encapsulating local database operations and exposing reactive `StateFlow` streams.

### 2.2 Sync Engine (`core/sync/`)
- **`SyncEngine.kt`**: Coordinates offline-first reconciliation between local Room DB and Cloud Firestore.
- **`P2PSyncManager.kt`**: Handles direct local Wi-Fi peer-to-peer data sync between paired phones without requiring internet access.
- **`TransferNotificationManager.kt`**: Manages the lifecycle of cross-member transfer notifications, banner state, and emoji reaction confirmations.

---

## 3. UI Modules & Dialog Specifications (`com.example.modules.dashboard`)

### 3.1 Dialogs Sub-package (`dashboard/dialogs/`)
1. **`IncomingTransferNotificationDialog.kt`**:
   - Displayed on recipient's device when a cross-member transfer is initiated.
   - Allows recipient to pick from 8 reaction emojis and confirm receipt.
2. **`TransferConfirmedNotificationDialog.kt`**:
   - Displayed on sender's device after recipient confirms transfer. Shows the selected emoji reaction.
3. **`MemberTransactionsDialog.kt`**:
   - Displays all transactions for a specific member (Husband/Wife) with summary cards and drill-down support.
4. **`CategoryTransactionsDialog.kt`**:
   - Filters and displays transactions associated with a specific spending category.
5. **`AddExpenseDialog.kt` & `AddGoalDialog.kt` & `DepositGoalDialog.kt` & `EditBudgetDialog.kt`**:
   - Input forms for quick expense logging, creating savings goals, depositing money into goals, and adjusting monthly budget targets.

### 3.2 Key Screens & Components
- **`DashboardScreen.kt`**: Primary navigation hub and top-level container hosting sub-screens (Analytics, Goals, Monthly Report, History, Net Worth, Transfer).
- **`TransferScreen.kt`**: Dedicated transfer UI with wallet pickers, instant fee calculation, and notification dispatch.
- **`AnalyticsScreen.kt`**: Comprehensive reports with pie charts, category breakdowns, and member spending cards.
- **`GoalsAndBudgetScreen.kt`**: Budget pacing gauge, category limits, and family savings targets.

---

## 4. Audit & Quality Assurance Scenarios

When performing code reviews or adding new features, audit against these 3 negative scenarios:
1. **Malformed/Null Data Input:** Verify that empty notes, 0 or negative transfer amounts, and missing category references fail gracefully without crashing.
2. **Offline/Sync Disconnection:** Verify that state updates commit locally to Room first (Optimistic UI) before trying network sync.
3. **UI Dead-End:** Ensure every dialog features an explicit `onDismiss` request and close action, preventing unclosable loading states.
