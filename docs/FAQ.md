# Frequently Asked Questions (FAQ)

### 1. Is FamilyLedger truly offline-first?
**Yes.** All household transactions, wallets, recurring bills, and ledger events are stored locally in an encrypted Room SQLite database powered by SQLCipher (AES-256). The app functions completely without internet access.

### 2. How does P2P Sync work without the internet?
When both partners are on the same Wi-Fi or mobile hotspot network, one device acts as a host server while the other connects directly over the local network. The transmission uses mutual pair code verification and conflict-free LWW (Last-Write-Wins) timestamp resolution.

### 3. How are secret stash (Vault) wallets protected?
Vault balances and records are masked from high-level balance cards and require passing the biometric or PIN lock screen overlay.

### 4. What is the SHA-256 Event Chaining mechanism?
Every mutation in the ledger generates a deterministic cryptographic `LedgerEvent`. Each event contains the SHA-256 hash of the preceding event, creating an immutable hash chain that prevents tampering or silent alteration of historical financial logs.

### 5. Why are there no third-party telemetry or ad SDKs?
FamilyLedger is designed with zero tracking and privacy-first principles. We do not integrate advertising SDKs, commercial trackers, or third-party behavioral profiling.
