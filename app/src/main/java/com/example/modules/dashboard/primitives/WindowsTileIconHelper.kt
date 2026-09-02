package com.example.modules.dashboard.primitives

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class WindowsIconTileItem(
    val iconName: String,
    val label: String,
    val icon: ImageVector,
    val tileColor: Color
)

object WindowsTileIconHelper {
    val presetIcons = listOf(
        WindowsIconTileItem("wallet", "Dompet / Tunai", Icons.Default.AccountBalanceWallet, Color(0xFF2563EB)),
        WindowsIconTileItem("bank", "Bank Transfer", Icons.Default.AccountBalance, Color(0xFF0284C7)),
        WindowsIconTileItem("card", "Kartu Kredit", Icons.Default.CreditCard, Color(0xFF7C3AED)),
        WindowsIconTileItem("ewallet", "E-Wallet", Icons.Default.Smartphone, Color(0xFF059669)),
        WindowsIconTileItem("vault", "Tabungan", Icons.Default.Savings, Color(0xFFD97706)),
        WindowsIconTileItem("food", "Makanan & Jajan", Icons.Default.Fastfood, Color(0xFFEA580C)),
        WindowsIconTileItem("shopping", "Belanja", Icons.Default.ShoppingCart, Color(0xFFEC4899)),
        WindowsIconTileItem("transport", "Transport & BBM", Icons.Default.DirectionsCar, Color(0xFF0284C7)),
        WindowsIconTileItem("bills", "Tagihan & Listrik", Icons.Default.Bolt, Color(0xFFCA8A04)),
        WindowsIconTileItem("salary", "Gaji & Komisi", Icons.Default.Payments, Color(0xFF16A34A)),
        WindowsIconTileItem("invest", "Investasi", Icons.AutoMirrored.Filled.TrendingUp, Color(0xFF0D9488)),
        WindowsIconTileItem("health", "Kesehatan & Obat", Icons.Default.MedicalServices, Color(0xFFE11D48)),
        WindowsIconTileItem("education", "Pendidikan", Icons.Default.School, Color(0xFF6366F1)),
        WindowsIconTileItem("entertainment", "Hiburan & Game", Icons.Default.SportsEsports, Color(0xFF8B5CF6)),
        WindowsIconTileItem("home", "Rumah & Properti", Icons.Default.Home, Color(0xFF475569)),
        WindowsIconTileItem("gift", "Hadiah & Bonus", Icons.Default.CardGiftcard, Color(0xFFDB2777))
    )

    fun getIconForItem(name: String, defaultType: String = ""): ImageVector {
        val lower = name.lowercase()
        return when {
            lower.contains("bca") || lower.contains("bank") || lower.contains("mandiri") -> Icons.Default.AccountBalance
            lower.contains("gopay") || lower.contains("ovo") || lower.contains("dana") || lower.contains("ewallet") -> Icons.Default.Smartphone
            lower.contains("tunai") || lower.contains("cash") || lower.contains("dompet") -> Icons.Default.AccountBalanceWallet
            lower.contains("makan") || lower.contains("kopi") || lower.contains("jajan") || lower.contains("kuliner") -> Icons.Default.Fastfood
            lower.contains("belanja") || lower.contains("shopee") || lower.contains("tokopedia") -> Icons.Default.ShoppingCart
            lower.contains("bensin") || lower.contains("transport") || lower.contains("mobil") || lower.contains("motor") -> Icons.Default.DirectionsCar
            lower.contains("listrik") || lower.contains("tagihan") || lower.contains("pulsa") -> Icons.Default.Bolt
            lower.contains("gaji") || lower.contains("bonus") || lower.contains("income") -> Icons.Default.Payments
            lower.contains("invest") || lower.contains("saham") || lower.contains("crypto") -> Icons.AutoMirrored.Filled.TrendingUp
            lower.contains("sehat") || lower.contains("obat") || lower.contains("dokter") -> Icons.Default.MedicalServices
            lower.contains("hiburan") || lower.contains("game") || lower.contains("nonton") -> Icons.Default.SportsEsports
            else -> if (defaultType == "Wallet") Icons.Default.AccountBalanceWallet else Icons.Default.Category
        }
    }
}
