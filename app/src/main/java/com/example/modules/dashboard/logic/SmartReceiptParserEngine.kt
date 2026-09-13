package com.example.modules.dashboard.logic

data class ParsedReceiptResult(
    val merchantName: String,
    val totalAmount: Long,
    val itemsSummary: String,
    val detectedCategoryKeyword: String,
    val suggestedWalletType: String,
    val isConfident: Boolean
)

object SmartReceiptParserEngine {

    fun parseReceiptText(rawText: String): ParsedReceiptResult {
        val cleanText = rawText.trim()
        if (cleanText.isBlank()) {
            return ParsedReceiptResult("Belanja Dapur", 0L, "Belanja Umum", "sayur", "Cash", false)
        }

        val lines = cleanText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        var merchant = "Belanja Supermarket / Pasar"
        var detectedTotal = 0L
        var categoryKeyword = "sayur"
        var walletType = "Cash"
        val detectedItems = mutableListOf<String>()

        val lowerText = cleanText.lowercase()
        when {
            lowerText.contains("indomaret") -> { merchant = "Indomaret"; walletType = "E-Wallet"; categoryKeyword = "sembako" }
            lowerText.contains("alfamart") || lowerText.contains("alfamidi") -> { merchant = "Alfamart"; walletType = "E-Wallet"; categoryKeyword = "sembako" }
            lowerText.contains("superindo") -> { merchant = "Super Indo"; walletType = "Bank"; categoryKeyword = "sayur" }
            lowerText.contains("hypermart") || lowerText.contains("transmart") -> { merchant = "Supermarket"; walletType = "Bank"; categoryKeyword = "sembako" }
            lowerText.contains("pasar") -> { merchant = "Pasar Tradisional"; walletType = "Cash"; categoryKeyword = "sayur" }
            lowerText.contains("sayur") -> { merchant = "Tukang Sayur Keliling"; walletType = "Cash"; categoryKeyword = "sayur" }
            lowerText.contains("apotek") || lowerText.contains("kimia farma") -> { merchant = "Apotek / Farmasi"; walletType = "E-Wallet"; categoryKeyword = "obat" }
            lines.isNotEmpty() -> merchant = lines.first().take(30)
        }

        // Detect Total with Priority (Explicit Total keywords > Tunai/Payment keywords)
        val totalExplicitRegex = Regex("""(?:grand\s*total|total(?:\s*belanja|\s*pembayaran)?|tagihan|subtotal|bayar)\s*[:=]?\s*(?:rp\.?\s*)?([0-9.,]+)""", RegexOption.IGNORE_CASE)
        for (line in lines.reversed()) {
            val match = totalExplicitRegex.find(line)
            if (match != null) {
                val parsed = match.groupValues[1].replace(".", "").replace(",", "").toLongOrNull() ?: 0L
                if (parsed > 0) { detectedTotal = parsed; break }
            }
        }

        if (detectedTotal == 0L) {
            val fallbackRegex = Regex("""(?:tunai|cash|debit|rp)\s*[:=]?\s*([0-9.,]+)""", RegexOption.IGNORE_CASE)
            for (line in lines.reversed()) {
                val match = fallbackRegex.find(line)
                if (match != null) {
                    val parsed = match.groupValues[1].replace(".", "").replace(",", "").toLongOrNull() ?: 0L
                    if (parsed > 0) { detectedTotal = parsed; break }
                }
            }
        }

        if (detectedTotal == 0L) {
            val standaloneNumRegex = Regex("""\b([1-9][0-9]{0,2}(?:\.[0-9]{3})+|[1-9][0-9]{3,7})\b""")
            var maxFound = 0L
            for (line in lines) {
                standaloneNumRegex.findAll(line).forEach { m ->
                    val num = m.value.replace(".", "").toLongOrNull() ?: 0L
                    if (num in 1000L..10000000L && num > maxFound) maxFound = num
                }
            }
            if (maxFound > 0) detectedTotal = maxFound
        }

        val itemKeywords = listOf("beras", "minyak", "telur", "susu", "sabun", "bayam", "kangkung", "ayam", "daging", "tempe", "tahu", "bumbu", "pampers", "gas")
        for (line in lines) {
            val lineLow = line.lowercase()
            for (kw in itemKeywords) {
                if (lineLow.contains(kw) && !detectedItems.contains(kw.replaceFirstChar { it.uppercase() })) {
                    detectedItems.add(kw.replaceFirstChar { it.uppercase() })
                }
            }
        }

        if (detectedItems.any { it.equals("susu", true) || it.equals("pampers", true) }) {
            categoryKeyword = "anak"
        } else if (detectedItems.any { it.equals("sabun", true) }) {
            categoryKeyword = "kebersihan"
        } else if (detectedItems.any { it.equals("beras", true) || it.equals("minyak", true) }) {
            categoryKeyword = "sembako"
        }

        val summary = if (detectedItems.isNotEmpty()) detectedItems.joinToString(", ") else "Belanja $merchant"

        return ParsedReceiptResult(
            merchantName = merchant,
            totalAmount = detectedTotal,
            itemsSummary = summary,
            detectedCategoryKeyword = categoryKeyword,
            suggestedWalletType = walletType,
            isConfident = detectedTotal > 0
        )
    }
}
