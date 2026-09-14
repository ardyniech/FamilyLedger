# FamilyLedger Roadmap

This document outlines the planned development milestones and features for **FamilyLedger**.

---

## 🎯 Current Milestone: Phase 1 — Enterprise Hardening & Open Source Readiness (v1.1)
- [x] Room Database v9 with foreign key cascade and complete composite indexing
- [x] Cryptographic SHA-256 event chaining for audit trails
- [x] Offline P2P Local Wi-Fi sync with socket timeouts
- [x] TLS Certificate Pinning on GitHub OTA updates
- [x] Biometric Auth & configurable AppLock auto-timeout
- [x] GitHub Actions CI with strict zero-bypass validation
- [x] Gradle Wrapper distribution (`gradlew` + wrapper JAR)

---

## 🚀 Phase 2 — Enhanced Offline Connectivity & Biometric Vault (v1.2)
- [ ] **mDNS / NSD Auto-Discovery**: Automatic peer discovery on local Wi-Fi without manual IP entry
- [ ] **Biometric Hardware Vault**: Decryption of secret stash wallets using AndroidX `BiometricPrompt` and Android Keystore AES-GCM keys
- [ ] **Encrypted JSON/Zip File Export**: Export and import complete household ledger snapshots via Android system file picker
- [ ] **PDF Tax & Monthly Report Generator**: Native PDF generation of household monthly financial summaries

---

## ☁️ Phase 3 — Cloud Sync & Multi-User Collaboration (v2.0)
- [ ] **Optional Firestore E2E Encrypted Sync**: Zero-knowledge encrypted cloud backup for couples
- [ ] **CRDT State Merging**: Conflict-free replicated data types for real-time concurrent balance updates
- [ ] **Push Notifications**: Low-latency transaction alerts when a partner logs an expense
- [ ] **Wear OS Companion**: Quick expense entry tile for Android smartwatches

---

## 💡 Propose a Feature
Have an idea? Open a [Feature Request](https://github.com/ardysyafii/familyledger/issues/new?template=feature_request.yml) on GitHub!
