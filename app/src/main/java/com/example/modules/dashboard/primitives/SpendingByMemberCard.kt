package com.example.modules.dashboard.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.theme.DesignTokens
import com.example.shared.utils.MemberRoleHelper
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SpendingByMemberCard(
    members: List<Member>,
    transactions: List<Transaction>,
    totalExpenses: Long,
    onMemberClick: ((Member) -> Unit)? = null
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DesignTokens.CornerRadius),
        colors = CardDefaults.cardColors(containerColor = DesignTokens.Surface),
        border = BorderStroke(1.dp, DesignTokens.BorderGlass),
        elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.ElevationSoft)
    ) {
        Column(
            modifier = Modifier.padding(DesignTokens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            members.forEach { member ->
                val tint = MemberRoleHelper.getRoleColor(member, members)
                val memberExpenses = transactions
                    .filter { it.memberId == member.id && it.amount < 0 }
                    .sumOf { -it.amount }
                val ratio = if (totalExpenses > 0L) (memberExpenses.toFloat() / totalExpenses.toFloat()) else 0f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (onMemberClick != null) Modifier.clickable { onMemberClick(member) } else Modifier),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(12.dp).background(tint, RoundedCornerShape(6.dp)))
                        Text(member.name, fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatter.format(memberExpenses), fontWeight = FontWeight.Bold, color = DesignTokens.TextPrimary)
                        Text("${(ratio * 100).toInt()}% dari total", style = MaterialTheme.typography.bodySmall, color = DesignTokens.TextSecondary)
                    }
                }
            }
        }
    }
}
