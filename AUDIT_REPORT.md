# FamilyLedger — Laporan Audit Keamanan & Kualitas Total

**Target:** `github.com/ardyniech/FamilyLedger` (Android, Kotlin + Jetpack Compose)
**Tanggal audit:** 2026-08-27
**Cakupan:** Seluruh repositori (191 file) — fokus fitur OTA, layer data/storage, sync engine (Firestore + P2P), autentikasi, konfigurasi/secrets, ViewModel/CSV, dan tests.
**Metode:** Clone lokal (`--depth 1`) + bacaan menyeluruh + 4 agen audit paralel.

---

## 1. Ringkasan Eksekutif

FamilyLedger adalah aplikasi ledger keuangan rumah tangga (offline-first) untuk pasangan. Secara fungsional aplikasi berjalan di skenario normal, namun audit menemukan **kelemahan akses data dan integritas uang yang parah**:

- **Isolasi antar rumah tangga rusak total** — seluruh data keuangan digembok hanya oleh *pair code* `FAM-8821` yang di-hardcode dan dapat ditebak.
- **Dua bug yang menggerakkan uang secara otomatis** — auto-pay berputar tak terbatas (menguras saldo) dan pembagian-nol di keypad yang lolos ke ledger sebagai `Infinity`.
- **Data keuangan tidak dienkripsi** di penyimpanan lokal.
- **Fitur OTA** memiliki klaim keamanan berlebihan; integritas sepenuhnya mengandalkan signature Android, dan mekanisme version-check rusak karena di-hardcode `"1.0"`.

**Tingkat temuan:** 6 Critical, 6 High, ~10 Medium, sejumlah Low/hygiene.

> Catatan: Repo saat ini **belum punya rilis (0 Releases)** dan Firebase tidak terinisialisasi (fallback local-only), sehingga sebagian jalur di atas belum aktif di runtime. Namun begitu di-deploy, kelemahan akses data (#1–#3) dan bug uang (#4–#5) berdampak langsung pada kerahasiaan & integritas finansial pengguna.

---

## 2. Temuan CRITICAL

### C-1. Pair code default `FAM-8821` sebagai satu-satunya pembatas akses
- `core/sync/SyncEngine.kt:20` — `private var currentPairCode: String = "FAM-8821"`
- `core/sync/SyncEngine.kt:40` — `fun startBackgroundSync(..., initialPairCode: String = "FAM-8821")`
- `core/sync/FirestoreOutboundSync.kt:15` — `firestore.collection("households").document(pairCode)`
- `core/sync/FirestoreInboundSync.kt:21` — `firestore.collection("households").document(pairCode)`
- `core/sync/p2p/P2PPayload.kt:105` (fallback pair code)
- **Masalah:** Pair code adalah ID dokumen Firestore sekaligus "rahasia" P2P. Kode sama di semua install → dua pasangan berbeda dapat menabrak dokumen yang sama dan saling melihat transaksi. Format `FAM-XXXX` (4 karakter) dapat di-brute-force; tidak ada rate-limit/lockout/kunci kripto.
- **Dampak:** Broken access control — baca/tulis seluruh ledger orang lain.

### C-2. Tidak ada Firestore Security Rules & otorisasi tidak diikat ke user
- Tidak ada file `firestore.rules` di repo.
- Path sync tidak pernah mengecek `FirebaseAuth.getCurrentUser()`. OAuth dipakai untuk login tapi **tidak di-enforce** di jalur data.
- **Dampak:** Selama tidak ada rules di console, koleksi rumah tangga world-readable/writable bagi siapa saja yang punya kode.

### C-3. P2P sync tanpa autentikasi peer, plaintext, tanpa tanda tangan
- `core/sync/p2p/P2POfflineSyncManager.kt:92-146` — buka `ServerSocket(8888)` mentah, `accept()` siapa saja; `launchClientHandler` import apa pun yang datang.
- `:42-90` (`importSyncPackage`) — tulis member/wallet/category/transaction langsung ke DAO **tanpa cek `pairCode` pengirim**, tanpa HMAC/signature, tanpa verifikasi pengirim.
- `:101,156` — transmisi cleartext TCP (hanya gzip+base64 JSON), tidak ada TLS.
- `P2PPayload.kt:183-188` — base64 digunzip tanpa batas ukuran (decompression-bomb → OOM).
- Tidak ada replay protection (`timestamp` tidak dicek).
- **Dampak:** Device jahat di LAN yang sama dapat injeksi/mengubah ledger korban, replay, atau crash receiver.

### C-4. Auto-pay berputar tak terbatas (menguras saldo)
- `modules/dashboard/DashboardViewModel.kt:53-73` — auto-pay dijadwalkan via `CsvDateParser.parseTimestamp(bill.dueDate)`.
- `logic/RecurringBillsManager.kt:16-19` — `dueDate` disimpan `"MMM dd, yyyy"` (mis. `"Aug 28, 2026"`).
- `csv/CsvDateParser.kt:7-25` — **tidak memiliki pola `"MMM dd, yyyy"`** → parse selalu gagal → fallback `System.currentTimeMillis()` (`:49`).
- Akibatnya `dueTime <= now` selalu benar → tiap tagihan dibayar saat startup; membayar memperbarui tanggal (re-emit `recurringBills`) yang lagi-lagi gagal diparse → dibayar **lagi**, loop tak berujung.

### C-5. Pembagian-nol di keypad → `Infinity` lolos validasi ke ledger
- `shared/utils/MathUtils.kt:38` — `x /= parseFactor()` tanpa guard `isFinite`/`!= 0` → `"5/0"` = `Double.POSITIVE_INFINITY`.
- `modules/dashboard/AddTransactionModal.kt:122-124` — hanya cek `if (result != null && result > 0)`; `Infinity > 0` = true → transaksi senilai `Infinity` masuk.
- **Dampak:** Merusak semua `sumOf`, saldo wallet, dan ekspor CSV.

### C-6. Database keuangan tidak dienkripsi (plaintext at rest)
- `core/storage/AppDatabase.kt:29-33` — `Room.databaseBuilder` polos; tidak ada SQLCipher/AndroidX Security/Keystore (`grep` → 0 match).
- `shared/models/Models.kt:9,33,57,63` — `pairCode`, saldo, amount, note disimpan plaintext.
- **Dampak:** Siapa saja dengan akses ADB/backup/file sistem membaca seluruh data finansial + rahasia pair code.

---

## 3. Temuan HIGH

- **H-1. Uang disimpan sebagai `Double`.** `Models.kt:57,33`; semua agregasi (`PeriodFilter.kt:71-72`, `MonthFilterHelper.kt:42-43`, `TransactionGrouping.kt:38-39`, `SmartCsvImportEngine.kt`) pakai `Double` → rounding error di app keuangan.
- **H-2. CSV injection saat ekspor.** `csv/CsvExportDialog.kt:47-48` hanya strip koma/baris; nilai berawalan `=`,`+`,`-`,`@` tidak dinetralkan → eksekusi formula di Excel/Sheets.
- **H-3. Fallback parse tanggal diam-diam ke "sekarang".** `CsvDateParser.kt:31,49` & `CsvDataConverter.kt:46,49` → transaksi salah periode, merusak agregat bulanan.
- **H-4. Baris pengeluaran tanpa kata kunci salah diklasifikasi Pemasukan.** `CsvPatternMatcher.kt:34` (selalu positif), `:40-49` (cabang `amount < 0` tidak terjangkau) → aliran dana terbalik, net worth rusak.
- **H-5. Google Sign-In rusak.** `core/auth/GoogleAuthService.kt:24-25` fallback placeholder `"family-ledger.apps.googleusercontent.com"`; tidak ada `google-services.json`/`default_web_client_id` di tree → OAuth tidak valid.
- **H-6. Transfer dana tidak atomik.** `DashboardViewModel.kt:142-143` dua `addTransaction` terpisah; kegagalan yang kedua → uang keluar sumber tapi tak sampai tujuan (saldo permanen tidak konsisten).

---

## 4. Temuan MEDIUM

### OTA (modul `modules/updater`)
- **M-1.** `currentVersionName = "1.0"` di-hardcode (`DashboardScreen.kt:~1348`) → version-check tidak bisa dipercaya; selalu menawarkan update.
- **M-2.** Tidak membandingkan `versionCode` → install bisa `INSTALL_FAILED_VERSION_DOWNGRADE` bila rilis tidak naikkan code (`build.gradle.kts: versionCode=1`).
- **M-3.** Verifikasi SHA-256 **opsional** — `ApkHashVerifier.verifyApkHash` `return true` bila `sha256Url` kosong. Checksum di-fetch dari saluran sama dgn APK → tidak lindungi dari kompromi repo.
- **M-4.** Pemilihan aset APK tidak pilih ABI → bisa terpasang APK salah-ABI.
- **M-5.** Path traversal pada `apkName` (`UpdateDownloader` → `File(updatesDir, apkName)`) tanpa sanitasi.
- **M-6.** Tidak ada cert-pinning; panggilan GitHub API tanpa token (limit 60/hr/IP).
- **M-7.** `isMandatory` diparse tapi tidak di-enforce.

### Data / Sync / Lainnya
- **M-8.** P2P conflict resolution insert-if-absent-by-id → update ke record existing dibuang (`P2POfflineSyncManager.kt:53-81`); asimetris dgn Firestore inbound yang pakai `updatedAt`.
- **M-9.** Tidak ada cek saldo/overdraft di transfer & pembayaran (`DashboardViewModel.kt:117-178`) → wallet bisa negatif; dipadu loop auto-pay = drain.
- **M-10.** `MonthlyOverviewCard.kt:30` bagi `budget` tanpa guard nol/negatif (`Infinity`/`NaN`).
- **M-11.** Duplicate detection lossy — `SmartCsvParser.kt:60,97-99` pakai `ts/60000` + `amount.toLong()` → transaksi berbeda di menit sama dgn nominal bulat sama dibuang.
- **M-12.** `fallbackToDestructiveMigration()` + `exportSchema=false` (`AppDatabase.kt:18,33`) → kehilangan data total bila schema naik versi.
- **M-13.** Tidak ada foreign key / index (`Models.kt`, `Daos.kt`) → full table scan; `mark*Synced` pakai `IN (:ids)` tak berbatas → >999 baris crash `SQLiteException`.
- **M-14.** `allowBackup=true` + rules sampel (`AndroidManifest.xml:23-25`, `backup_rules.xml`, `data_extraction_rules.xml`) — berisiko untuk data finansial/token.
- **M-15.** `REQUEST_INSTALL_PACKAGES` terlalu luas (`AndroidManifest.xml:15`).
- **M-16.** Test coverage trivial — `DashboardLogicTest`, `OtaUpdateEngineTest`, `SmartCsvEngineTest` hanya assert identitas/nol; tidak menutupi math uang, transfer, auto-pay, Infinity, CSV injection.

---

## 5. Temuan LOW / Hygiene
- Debug signing pakai default AOSP (`build.gradle.kts:34-39`); release `isMinifyEnabled=false`; `logging-interceptor` masuk implementasi; `googleServices.missing.passthrough=true` menyembunyikan config hilang; Compose BOM stale (`libs.versions.toml:12`); permission tak terpakai (NFC/`BLUETOOTH_ADMIN`); `println` di `SemVerComparator`; coroutine/threading P2P kurang rapi (`runBlocking` di `Thread`).

### Yang sudah BENAR (positif)
- Tidak ada secret/API key di-commit (tidak ada `google-services.json`, `*.keystore`, token di-log).
- Semua `@Query` Room pakai bind parameter → **tidak ada SQL injection**.
- FileProvider `exported=false`; `targetSdk=36` → cleartext dimatikan default.
- `MainActivity` `exported=true` hanya karena LAUNCHER (wajar).

---

## 6. Rekomendasi Prioritas (urut)

| # | Prioritas | Tindakan |
|---|-----------|----------|
| 1 | CRITICAL | Ganti `FAM-8821` dgn secret per-household entropy tinggi + ikat akses Firestore ke `FirebaseAuth` UID via **`firestore.rules`** nyata. (C-1, C-2) |
| 2 | CRITICAL | Amankan P2P: autentikasi peer (challenge/HMAC shared secret), verifikasi `pkg.pairCode`, TLS/offline channel terverifikasi, batas decompress, replay protection. (C-3) |
| 3 | CRITICAL | Tambah pola `"MMM dd, yyyy"` ke `CsvDateParser` + **jangan** fallback ke `now()` (lempar error). (C-4) |
| 4 | CRITICAL | Guard pembagian di `MathUtils` (`isFinite`/≠0) + validasi `AddTransactionModal` (`result.isFinite() && result > 0`). (C-5) |
| 5 | CRITICAL | Enkripsi DB at rest (SQLCipher/AndroidX Security) + konfigurasi backup exclude data finansial/token. (C-6) |
| 6 | HIGH | Uang pakai `BigDecimal`/cent `Long`; perbaiki klasifikasi Income/Expense CSV; netralkan CSV injection; transfer jadi satu `@Transaction` atomik. (H-1,H-2,H-4,H-6) |
| 7 | HIGH | Perbaiki Google Sign-In (siapkan `google-services.json` + `default_web_client_id` valid). (H-5) |
| 8 | MEDIUM | OTA: `BuildConfig.VERSION_NAME`+`versionCode`, wajibkan checksum, pilih APK per ABI, sanitasi nama file, enforce `isMandatory`. (M-1..M-7) |
| 9 | MEDIUM | Data layer: foreign key, index, migrasi nyata, batasi `IN (:ids)`, cek saldo/overdraft, guard divider. (M-8..M-13) |
| 10 | LOW | Hygiene: minify release, pasang rules backup, cabut permission tak perlu, perbaiki test. (M-14..M-16, LOW) |

---

## 7. Kesimpulan
Aplikasi memiliki UI/arsitektur yang rapi (Clean Arch + MVVM + Compose), namun **postur keamanannya tidak layak untuk data finansial nyata** pada kondisi saat ini. Dua kategori harus diselesaikan sebelum rilis publik: (a) **isolasi & otorisasi data antar pengguna** (C-1..C-3), dan (b) **integritas uang** (C-4, C-5, H-1, H-6). Fitur OTA perlu direvisi agar klaim keamanannya sesuai kenyataan (M-1..M-7).
