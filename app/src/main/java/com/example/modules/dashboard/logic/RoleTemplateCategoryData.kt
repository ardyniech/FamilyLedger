package com.example.modules.dashboard.logic

import com.example.shared.models.Category
import com.example.shared.models.CategoryGroup
import com.example.shared.models.HouseholdRole

object RoleTemplateCategoryData {

    private val templates: Map<HouseholdRole, RoleTemplateDefinition> = mapOf(
        HouseholdRole.SUAMI to RoleTemplateDefinition(
            role = HouseholdRole.SUAMI,
            themeName = "Midnight Cobalt & Slate Blue",
            groups = listOf(
                CategoryGroup("cg_suami_tanggung", "Tanggung Jawab Kepala Keluarga", "#2563EB", "🛡️", "Kewajiban nafkah, tagihan utama, & proteksi"),
                CategoryGroup("cg_suami_cicilan", "KPR, Cicilan & Kewajiban Tetap", "#EF4444", "📑", "Cicilan rumah, kendaraan & utang formal"),
                CategoryGroup("cg_suami_operasional", "Operasional & Transportasi Suami", "#0284C7", "🚗", "BBM, servis kendaraan, pulsa & mobilitas"),
                CategoryGroup("cg_suami_simpanan", "Dana Darurat & Investasi Masa Depan", "#10B981", "💎", "Tabungan perlindungan keluarga & masa depan"),
                CategoryGroup("cg_suami_pribadi", "Kebutuhan Pribadi Suami", "#64748B", "☕", "Kopi, hobi & kebutuhan personal suami")
            ),
            categories = listOf(
                Category("c_suami_nafkah", "Nafkah Istri & Uang Belanja", "Expense", groupId = "cg_suami_tanggung"),
                Category("c_suami_kpr", "Cicilan KPR / Sewa Rumah", "Expense", groupId = "cg_suami_cicilan"),
                Category("c_suami_pln", "Listrik PLN & Token", "Expense", groupId = "cg_suami_tanggung"),
                Category("c_suami_wifi", "Internet Wifi & Pulsa Rumah", "Expense", groupId = "cg_suami_tanggung"),
                Category("c_suami_pdam", "PDAM & Air Galon", "Expense", groupId = "cg_suami_tanggung"),
                Category("c_suami_kendaraan", "Cicilan / Servis Kendaraan", "Expense", groupId = "cg_suami_cicilan"),
                Category("c_suami_bbm", "Bahan Bakar & Tol / Transportasi", "Expense", groupId = "cg_suami_operasional"),
                Category("c_suami_spp", "Pendidikan & SPP Sekolah Anak", "Expense", groupId = "cg_suami_tanggung"),
                Category("c_suami_darurat", "Dana Darurat & Asuransi Keluarga", "Expense", groupId = "cg_suami_simpanan", isSavings = true),
                Category("c_suami_ngopi", "Kopi Kerja & Jajan Suami", "Expense", groupId = "cg_suami_pribadi")
            ),
            sampleHighlights = listOf(
                "🛡️ Nafkah Istri (Uang Belanja)",
                "📑 Cicilan KPR / Sewa Rumah",
                "⚡ Listrik PLN & Air PDAM",
                "📶 Internet Wifi",
                "🚗 BBM & Servis Kendaraan"
            )
        ),
        HouseholdRole.ISTRI to RoleTemplateDefinition(
            role = HouseholdRole.ISTRI,
            themeName = "Pink Blossom & Peach Cerah",
            groups = listOf(
                CategoryGroup("cg_istri_dapur", "Kebutuhan Dapur & Sembako (Uang Belanja)", "#EC4899", "🍳", "Sayur, lauk harian, beras, minyak & bumbu"),
                CategoryGroup("cg_istri_rumah", "Perlengkapan & Kebersihan Rumah Tangga", "#F43F5E", "🧼", "Sabun, deterjen, pewangi & perlengkapan rumah"),
                CategoryGroup("cg_istri_anak", "Kebutuhan Anak & Perlengkapan Sekolah", "#FB7185", "🍼", "Susu, pampers, vitamin & keperluan anak"),
                CategoryGroup("cg_istri_sosial", "Sosial, Arisan & Jajan Keluarga", "#F59E0B", "🧁", "Jajan bareng, arisan RT & sedekah"),
                CategoryGroup("cg_istri_pribadi", "Self-Care & Tabungan Cadangan Istri", "#8B5CF6", "💄", "Skincare, perawatan diri & kas cadangan dapur")
            ),
            categories = listOf(
                Category("c_istri_sayur", "Belanja Sayur Mayur & Lauk Harian", "Expense", groupId = "cg_istri_dapur"),
                Category("c_istri_sembako", "Beras, Minyak & Sembako Bulanan", "Expense", groupId = "cg_istri_dapur"),
                Category("c_istri_bumbu", "Gas Elpiji & Bumbu Dapur", "Expense", groupId = "cg_istri_dapur"),
                Category("c_istri_kebersihan", "Sabun Cuci, Deterjen & Kebersihan Rumah", "Expense", groupId = "cg_istri_rumah"),
                Category("c_istri_anak_susu", "Susu & Pampers Bayi", "Expense", groupId = "cg_istri_anak"),
                Category("c_istri_anak_sekolah", "Uang Saku & Les / Buku Anak", "Expense", groupId = "cg_istri_anak"),
                Category("c_istri_obat", "Obat-Obatan & Vitamin Keluarga", "Expense", groupId = "cg_istri_rumah"),
                Category("c_istri_arisan", "Arisan RT & Lingkungan", "Expense", groupId = "cg_istri_sosial"),
                Category("c_istri_jajan", "Jajan Bareng Anak & Santai", "Expense", groupId = "cg_istri_sosial"),
                Category("c_istri_skincare", "Skincare & Self-Care Istri", "Expense", groupId = "cg_istri_pribadi"),
                Category("c_istri_tabung", "Celengan Cadangan Dapur", "Expense", groupId = "cg_istri_pribadi", isSavings = true)
            ),
            sampleHighlights = listOf(
                "🍳 Belanja Dapur & Sayur",
                "🍚 Beras & Sembako Bulanan",
                "🍼 Kebutuhan Anak & Susu",
                "🧼 Perlengkapan Rumah",
                "💄 Self-Care & Tabungan Dapur"
            )
        )
    )

    fun getTemplate(role: HouseholdRole): RoleTemplateDefinition {
        return templates[role] ?: templates.getValue(HouseholdRole.SUAMI)
    }

    fun getGroupsForRole(role: HouseholdRole): List<CategoryGroup> = getTemplate(role).groups

    fun getCategoriesForRole(role: HouseholdRole): List<Category> = getTemplate(role).categories

    fun getSampleHighlights(role: HouseholdRole): List<String> = getTemplate(role).sampleHighlights
}
