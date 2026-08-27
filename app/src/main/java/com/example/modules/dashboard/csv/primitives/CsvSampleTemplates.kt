package com.example.modules.dashboard.csv.primitives

data class SampleTemplate(
    val title: String,
    val description: String,
    val sampleCsv: String
)

object CsvSampleTemplates {
    val templates = listOf(
        SampleTemplate(
            title = "Format Transaksi Agustus 2026 (Live)",
            description = "Format standar harian berjalan: Waktu, Jenis, Nominal, Kategori, Dompet, Catatan",
            sampleCsv = """
"Date","Type","Amount","Category","Account","Notes"
"Aug 01, 2026 8:45 AM","(-) Expense","12000","rokok","Cash",""
"Aug 01, 2026 12:30 PM","(-) Expense","25000","makan","Cash","nasi padang"
"Aug 01, 2026 7:15 PM","(+) Income","350000","gojek","Cash","order gacor"
"Aug 02, 2026 9:00 AM","(-) Expense","15000","ngopi","Cash","kopi jahe"
"Aug 02, 2026 2:00 PM","(*) Transfer","100000","  -  ","Cash->Deina","belanja dapur"
"Aug 03, 2026 8:30 PM","(-) Expense","45000","jajan bareng","Cash","martabak manis"
            """.trimIndent()
        ),
        SampleTemplate(
            title = "Format Semicolon / Money Manager",
            description = "Pemisah titik-koma (;) dengan format tanggal YYYY-MM-DD",
            sampleCsv = """
Date;Type;Amount;Category;Account;Note
2026-08-04 10:00;Expense;12000;rokok;Cash;surya pro
2026-08-04 13:00;Expense;20000;makan;Kasbon;warteg
2026-08-04 20:00;Income;180000;maxim;Cash;narik malam
2026-08-05 09:30;Expense;30000;maxim saldo;Cash;topup saldo
            """.trimIndent()
        ),
        SampleTemplate(
            title = "Format Indonesia / Rupiah (Titik-Koma)",
            description = "Format nominal Rp dan tanggal dd/MM/yyyy",
            sampleCsv = """
Tanggal;Tipe;Nominal;Kategori;Dompet;Keterangan
06/08/2026 08:30;Pengeluaran;Rp 15.000;makan;Cash;pecel lele
06/08/2026 11:00;Pengeluaran;Rp 12.000;rokok;Cash;
06/08/2026 21:00;Pemasukan;Rp 250.000;grab;Cash;tips customer
07/08/2026 14:00;Transfer;Rp 50.000;  -  ;Cash->Kasbon;bayar hutang warung
            """.trimIndent()
        )
    )
}
