# Family Ledgers: Harmoni Melalui Transparansi 🕊️

> *"Banyak keluarga bertengkar gara-gara keuangan. Suami menuduh istri kurang benar mengelola, istri merasa suami tidak terbuka soal pendapatan. Aplikasi ini lahir dari pengalaman personal untuk memitigasi hal tersebut. Tujuannya satu: agar seluruh keluarga berdamai dan mencapai harmoni."*

Family Ledgers bukan sekadar aplikasi pencatat keuangan biasa. Ini adalah **jembatan komunikasi** bagi pasangan suami istri. Aplikasi ini dirancang secara khusus untuk menghilangkan asumsi, kecurigaan, dan miskomunikasi terkait arus kas rumah tangga. Dengan visibilitas data yang penuh dan *real-time*, suami menjadi lebih *aware* terhadap tingginya kebutuhan operasional rumah tangga, dan istri mengetahui secara transparan dari mana sumber dana serta alokasinya.

---

## 🏗️ Arsitektur Inti: Offline-First & Serverless Sync

Karena aplikasi ini menyangkut sensitivitas emosional dan finansial rumah tangga, data yang ditampilkan di perangkat Suami dan Istri **wajib selalu presisi, sinkron, dan akurat**. Keterlambatan sinkronisasi atau perbedaan angka bisa memicu perdebatan ("cek-cok"). Oleh karena itu, aplikasi ini diarsiteki dengan pendekatan **Enterprise Offline-First Serverless**:

1.  **Local-First Mutation (Room SQLite):**
    Semua interaksi dan mutasi data (Pengeluaran, Pemasukan, Transfer) diikat langsung ke memori lokal perangkat (Room Database). Aplikasi tidak akan pernah merespons lambat (infinite loading) atau gagal menyimpan meskipun pengguna berada di area tanpa sinyal (Offline mode). Ini menjaga kenyamanan psikologis pengguna saat mencatat transaksi.

2.  **Seamless Background Sync & Reconciliation:**
    Sinkronisasi ke perangkat pasangan akan dikelola di belakang layar (*background worker*) secara transparan. Saat perangkat online, mutasi lokal akan dikirim ke *serverless backend* (misal: Firebase Firestore / Supabase). Sistem akan menggunakan resolusi konflik berbasis *timestamp* dan *Unique ID* sehingga jika Suami dan Istri mencatat pengeluaran di detik yang bersamaan, tidak ada data yang saling menimpa (*Data Loss = 0%*).
    
3.  **Event-Driven Real-Time Updates:**
    Dengan *socket/subscriptions* pada database *serverless*, antarmuka (UI) Suami akan otomatis me-*refresh* diri (*Reactive State*) dalam hitungan milidetik setelah Istri menyimpan transaksi, dan sebaliknya. Komunikasi angka terjadi secara instan.

---

## 🛡️ Prinsip Desain: Menjaga Konsistensi, Kepercayaan, dan Akuntansi Sah

Untuk menghindari celah ambiguitas yang sering menjadi sumber perdebatan, aplikasi mengunci celah *human-error* dengan standar akuntansi yang ketat:

-   **Zero Manual String Input (Kategori Terpusat):** 
    Pengguna **tidak diizinkan** mengetik nama kategori/dompet secara manual saat membuat transaksi (mencegah duplikasi kotor seperti "makan", "Makan", "MAKAN"). Semua entitas dikelola secara terpusat (Strict Management). Laporan bulanan dijamin akurat dan mudah dievaluasi bersama.
-   **Hak Kelola Entitas Transparan (Cross-Member Transfer):** 
    Uang yang ditransfer dari dompet/rekening Suami ke dompet Istri **otomatis dicatat sebagai "Pengeluaran" (Expense) bagi Suami dan "Pemasukan" (Income) bagi Istri**. Logika akuntansi ini menengahi secara adil: uang telah berpindah hak kelola. Istri memegang otoritas penuh atas dana tersebut, dan suami melihat dana tersebut telah dialokasikan (bukan mengendap di asetnya).
-   **Immutable Audit Trail:** 
    Setiap transaksi merekam jejak *siapa* yang mengeksekusinya (Member ID). Transparansi ini mencegah tuduhan tanpa bukti. Angka yang berbicara.

---

## 🚀 Roadmap Pengembangan (Visi Keharmonisan)

1.  **Phase 1: Local Integrity & UI Foundation (Tahap Berjalan)**
    Membangun arsitektur *local database* (Room), *strict categorization*, manajemen dompet, dan kalkulasi mutasi perpindahan dana yang akurat secara *offline*.
2.  **Phase 2: The Serverless Cloud Bridge (Mendatang)**
    Membangun lapisan autentikasi (Google Sign-In) dan sinkronisasi *real-time* menggunakan Serverless Database. Pasangan dapat melakukan *Pairing Code* untuk menyatukan dua akun berbeda ke dalam satu **Household ID** yang terenkripsi.
3.  **Phase 3: Smart Financial Mediation**
    Modul analitik yang merangkum *habit* pengeluaran, menyajikan fakta secara visual yang menetralkan perdebatan, dan memandu pasangan merencanakan tujuan masa depan (Tabungan Anak, Rumah, dll) dengan visi yang sama.

---
*Dikodekan dengan empati mendalam untuk keluarga-keluarga yang ingin bertumbuh bersama dalam visi yang sama, kepercayaan, dan rasa saling menghargai.*
