# FamilyLedger - Household Financial Management Android App

**FamilyLedger** is a modern, high-performance, offline-first household financial management application built natively for Android using **Kotlin** and **Jetpack Compose**. Designed specifically for couples (Husband & Wife), FamilyLedger promotes financial transparency, joint budgeting, individual wallet tracking, cross-member transfer notifications with interactive emoji reactions, and real-time P2P & cloud synchronization.

---

## 🚀 Key Features

### 1. 👥 Multi-Member & Household Pairing
- **Role-Based Profiles:** Support for Husband (*Suami*) & Wife (*Istri*) profiles with dedicated visual identity and role badges.
- **Household Pairing System:** Pair devices seamlessly via unique Household Code, Local P2P Wi-Fi Sync, or Google Firebase Cloud Sync.
- **Active Member Switcher:** Quickly toggle between profiles to view individual or combined financial positions.

### 2. 💳 Wallet & Net Worth Management
- **Multi-Wallet Support:** Manage Cash, Bank accounts, E-Wallets (GoPay, OVO, ShopeePay, Dana), and Savings accounts.
- **Wallet Ownership:** Assign wallet ownership to Husband, Wife, or Joint Household.
- **Net Worth Tracking:** Real-time net worth visualization with individual balance breakdown.

### 3. 💸 Inter-Account & Cross-Member Transfer Engine
- **Cross-Member Transfers:** Transfer funds between Husband and Wife wallets.
- **Real-Time Notification & Confirmation:**
  - When Husband transfers money to Wife's wallet, an instant incoming transfer alert pops up on the Wife's device.
  - The Wife can select a custom reaction emoji (❤️, 😘, 🤲, 🙏, 🥰, 💸, 🎁, 💖) and tap **Confirm**.
  - A return notification immediately notifies the Husband's device with the Wife's selected emoji reaction.

### 4. 📊 Budgeting & Financial Goals
- **Category Budget Allocation:** Set monthly spending limits per category (Food, Utilities, Shopping, Entertainment, etc.).
- **Budget Pacing Indicator:** Visual gauge and indicator to track daily spending velocity vs remaining budget.
- **Family Savings Goals:** Interactive savings cards for goals (e.g., House Down Payment, Vacation, Emergency Fund) with deposit flow.

### 5. 📈 Analytics & Reports
- **Monthly Spending Trends:** Category-wise pie chart breakdown and member-wise spending proportion.
- **Interactive Drill-down:** Tap any category or member card to inspect detailed transaction logs via dedicated dialogs.
- **Monthly Advice Engine:** Smart insights on spending health, savings rate, and remaining budget days.

### 6. 📁 Data Import/Export & Local-First Sync
- **Smart CSV Import & Export:** Backup transactions or import bank statements directly via CSV.
- **Local-First Architecture:** Offline mutation using Room SQLite database with optimistic UI updates.
- **Multi-Engine Sync:** Dual sync via P2P (Local Network) and Google Cloud Firestore.

### 7. 🔄 Self-OTA Update Engine
- **In-App Checking:** Tap a button in the pairing screen to check if a newer version exists in the target GitHub repository (`ardyniech/FamilyLedger`).
- **Secure Chunked Download:** Multi-threaded download engine with automatic retries and live speed meter.
- **SHA-256 Verification:** Automatic cryptographic verification of downloaded APK files prior to installation.
- **Dynamic Installer Action:** Launches standard Android application installation prompt using secure FileProviders.

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Architecture:** Clean Architecture + Unidirectional Data Flow (UDF) + MVVM
- **Database / Persistence:** Room Database (SQLite) with KSP
- **Async & Reactive:** Kotlin Coroutines & StateFlow
- **Serialization:** kotlinx.serialization
- **Design System:** `DesignTokens.kt` with dynamic glassmorphism and modern light palette

---

## 📁 Project Structure

```
app/src/main/java/com/example/
├── core/
│   ├── auth/                      # Authentication Manager & Google Sign-In
│   ├── storage/                   # Room Database, DAOs, Entities, Repository
│   └── sync/                      # Sync Engine (P2P & Firestore) + Notification Manager
├── shared/
│   ├── atoms/                     # Base UI components & Design Tokens
│   ├── models/                    # Shared immutable domain data models
│   └── theme/                     # App central Theme & Design System
├── modules/
│   ├── updater/                   # Self-OTA Update Engine (Models, Services, Downloader, UI)
│   └── dashboard/
│       ├── dialogs/               # Transaction, Member, Category & Notification Dialogs
│       ├── logic/                 # Pure domain filter & pacing calculation utilities
│       ├── management/            # Category, Wallet & Transfer Management screens
│       ├── primitives/            # Modular reusable UI cards, banners & widgets
│       ├── subscreens/            # Full-page secondary analytics & report screens
│       ├── DashboardScreen.kt     # Main Dashboard container & router
│       └── DashboardViewModel.kt  # Central ViewModel managing state flow
```

---

## 🧪 Testing & Verification

Run local unit tests and screenshot verifications:
```bash
# Execute local unit tests
gradle :app:testDebugUnitTest

# Compile applet
compile_applet
```

---

## 📝 License & GitHub Sync

This project is configured for seamless GitHub synchronization under the **FamilyLedger** repository name.
- **Package Namespace:** `com.example`
- **Application ID:** `com.aistudio.familyledger.app`
