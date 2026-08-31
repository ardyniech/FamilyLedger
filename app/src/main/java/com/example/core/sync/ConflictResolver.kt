package com.example.core.sync

import com.example.core.storage.LedgerEventEntity
import com.example.shared.models.Category
import com.example.shared.models.Member
import com.example.shared.models.Transaction
import com.example.shared.models.WalletAccount

enum class ConflictResolution {
    KEEP_LOCAL,
    ACCEPT_INCOMING
}

object ConflictResolver {
    fun resolveTransaction(local: Transaction?, incoming: Transaction): ConflictResolution {
        if (local == null) return ConflictResolution.ACCEPT_INCOMING
        return if (incoming.updatedAt > local.updatedAt) {
            ConflictResolution.ACCEPT_INCOMING
        } else if (incoming.updatedAt < local.updatedAt) {
            ConflictResolution.KEEP_LOCAL
        } else {
            if (incoming.id > local.id) ConflictResolution.ACCEPT_INCOMING else ConflictResolution.KEEP_LOCAL
        }
    }

    fun resolveWallet(local: WalletAccount?, incoming: WalletAccount): ConflictResolution {
        if (local == null) return ConflictResolution.ACCEPT_INCOMING
        return if (incoming.updatedAt > local.updatedAt) {
            ConflictResolution.ACCEPT_INCOMING
        } else {
            ConflictResolution.KEEP_LOCAL
        }
    }

    fun resolveCategory(local: Category?, incoming: Category): ConflictResolution {
        if (local == null) return ConflictResolution.ACCEPT_INCOMING
        return if (incoming.updatedAt > local.updatedAt) {
            ConflictResolution.ACCEPT_INCOMING
        } else {
            ConflictResolution.KEEP_LOCAL
        }
    }

    fun resolveMember(local: Member?, incoming: Member): ConflictResolution {
        if (local == null) return ConflictResolution.ACCEPT_INCOMING
        return if (incoming.updatedAt > local.updatedAt) {
            ConflictResolution.ACCEPT_INCOMING
        } else {
            ConflictResolution.KEEP_LOCAL
        }
    }

    fun resolveLedgerEvent(local: LedgerEventEntity?, incoming: LedgerEventEntity): ConflictResolution {
        if (local == null) return ConflictResolution.ACCEPT_INCOMING
        return if (incoming.logicalClock > local.logicalClock) {
            ConflictResolution.ACCEPT_INCOMING
        } else {
            ConflictResolution.KEEP_LOCAL
        }
    }
}
