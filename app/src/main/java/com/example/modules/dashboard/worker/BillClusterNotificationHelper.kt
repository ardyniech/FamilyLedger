package com.example.modules.dashboard.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.modules.dashboard.logic.BillCluster
import com.example.shared.utils.MathUtils

object BillClusterNotificationHelper {
    const val CHANNEL_ID = "channel_family_bill_clusters"
    private const val CHANNEL_NAME = "Pengingat Tagihan & KPR (H-3 / H-1)"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Notifikasi otomatis sebelum tanggal cluster tagihan dan cicilan tiba"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun showClusterAlert(context: Context, cluster: BillCluster, daysLeft: Int) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("NAVIGATE_TO", "DEBT_TRACKER")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            cluster.startDay,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (daysLeft) {
            0 -> "🚨 Hari Ini! Tagihan Jatuh Tempo"
            1 -> "⚠️ Peringatan H-1: Tagihan Besok!"
            3 -> "🔔 Peringatan H-3: Persiapkan Dana Tagihan"
            else -> "📅 Tagihan Mendatang (${daysLeft} hari lagi)"
        }

        val formattedAmount = MathUtils.formatRupiah(cluster.totalAmount)
        val itemsSummary = cluster.items.take(2).joinToString(", ") { it.title }
        val moreText = if (cluster.items.size > 2) " (+${cluster.items.size - 2} lainnya)" else ""
        val content = "$itemsSummary$moreText senilai $formattedAmount jatuh tempo tgl ${cluster.startDay}-${cluster.endDay}."

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$content\nPastikan saldo rekening mencukupi agar tidak terkena penalti denda."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(cluster.startDay * 100 + daysLeft, builder.build())
        } catch (_: SecurityException) {
            // Permission POST_NOTIFICATIONS may not be granted by user yet
        }
    }
}
