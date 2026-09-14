# Changelog

All notable changes to **FamilyLedger** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.1.0] - 2026-09-14

### Added
- **Database Indexing & Foreign Key Constraints**: Complete Room schema hardening with composite and unique indices on all 8 tables (`households`, `members`, `wallet_accounts`, `category_groups`, `categories`, `transactions`, `household_expenses`, `ledger_events`, `internal_transfers`).
- **Cryptographic Event Sourcing**: Deterministic SHA-256 hash chaining on `ledger_events` for offline fraud audit trails.
- **TLS Certificate Pinning**: `SecureHttpClientProvider` with SHA-256 public key pinning for GitHub release updates and downloads.
- **P2P Local Wi-Fi Sync Engine**: Offline peer-to-peer ledger synchronization over local Wi-Fi with timeout-protected socket accept loops.
- **AppLock & Auto-Lock Timeout**: Configurable session lock timeout with biometric authentication support and haptic feedback.
- **Modular Data Importer**: Sample financial profile importer for seamless test-driving without mock data.
- **GitHub Templates & Open Source Infrastructure**: Issue templates for bug reports and feature requests, PR template, Dependabot, and CI pipeline hardening.

### Changed
- Refactored all architecture layers to adhere strictly to <=125 LOC per file rules.
- Upgraded Room Database to schema version 9 with continuous migration chain (`MIGRATION_1_2` through `MIGRATION_8_9`).
- Enforced strict CI testing rules (`testDebugUnitTest` and `lint` without failure bypass).
- Network security configuration enforcing `cleartextTrafficPermitted="false"`.

### Fixed
- Resolved missing `gradlew` and `gradle-wrapper.jar` wrapper distribution.
- Resolved orphaned member/transaction records via foreign key cascading and household initialization.
- Mitigated unhandled socket timeout blocking in P2P sync server.

---

## [1.0.0] - 2026-08-01

### Added
- Initial release of FamilyLedger — local-first double-entry household bookkeeping.
- Multi-wallet accounts (Cash, Bank, E-Wallet, Secret Vault).
- Real-time budget tracking, categorization, and transaction log.
- SQLCipher local AES-256 database encryption.
