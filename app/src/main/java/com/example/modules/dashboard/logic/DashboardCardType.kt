package com.example.modules.dashboard.logic

enum class DashboardCardType(
    val title: String,
    val description: String,
    val isDefaultEnabled: Boolean = true
) {
    HERO_BALANCE(
        title = "Ringkasan Saldo & Runway",
        description = "Total Saldo, Net Worth, dan Proyeksi Runway Keuangan"
    ),
    QUICK_ACTIONS(
        title = "Tombol Aksi Cepat",
        description = "Akses Pindah Buku / Transfer, Dompet & Kategori"
    ),
    FINANCIAL_HEALTH(
        title = "Analitik & Criticism Action",
        description = "Analitik Ringkasan, Kritik Keuangan & Tombol Aksi"
    ),
    WALLETS_CAROUSEL(
        title = "Daftar Akun & Dompet",
        description = "Carousel Kartu Dompet & Ringkasan Per Anggota"
    ),
    ACTIVE_BANNERS(
        title = "Notifikasi & Alert Aktif",
        description = "Notifikasi Transfer Masuk & Peringatan Anggaran Terlampaui"
    ),
    RECENT_TRANSACTIONS(
        title = "Transaksi Terkini",
        description = "Daftar Riwayat Transaksi Terbaru yang Terkelompokkan"
    );

    companion object {
        fun getDefaultList(): List<DashboardCardType> = values().toList()
    }
}
