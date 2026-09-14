# Installation & Development Setup Guide

This guide walks you through setting up a local development environment for **FamilyLedger**.

---

## 📋 Prerequisites

- **JDK**: Java Development Kit 17 or 21 (Recommended: OpenJDK 17)
- **Android SDK**: API Level 34 / 36 with Android SDK Platform-Tools
- **Android Studio**: Ladybug (2024.2.1) or newer
- **Gradle**: 9.3.1 (Handled automatically via `./gradlew`)

---

## 🛠️ Step-by-Step Setup

### 1. Clone the Repository
```bash
git clone https://github.com/ardysyafii/familyledger.git
cd familyledger
```

### 2. Verify Gradle Wrapper & Build
```bash
./gradlew --version
./gradlew assembleDebug
```

### 3. Run Unit Tests
```bash
./gradlew testDebugUnitTest
```

---

## 🔐 Keystore Setup for Release Builds

By default, debug builds use the bundled debug keystore. For release signing:

1. Generate your release keystore:
   ```bash
   keytool -genkey -v -keystore my-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
   ```
2. Set environment variables:
   ```bash
   export KEYSTORE_PATH="/path/to/my-upload-key.jks"
   export STORE_PASSWORD="your-store-password"
   export KEY_PASSWORD="your-key-password"
   ```
3. Build the release APK:
   ```bash
   ./gradlew assembleRelease
   ```

---

## ☁️ Optional Firebase Cloud Sync Setup

If you wish to enable Google Authentication & Firestore Cloud Sync:

1. Create a project in [Firebase Console](https://console.firebase.google.com).
2. Register an Android app with package name `com.aistudio.familyledger.abcdxy` (or your custom package ID).
3. Download `google-services.json` and place it in the `app/` directory:
   ```
   app/google-services.json
   ```
4. Deploy security rules from the root `firestore.rules`:
   ```bash
   firebase deploy --only firestore:rules
   ```
