package com.example.core.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerAuditDao {
    @Query("SELECT * FROM ledger_events ORDER BY logicalClock DESC, createdAt DESC")
    fun getAllLedgerEvents(): Flow<List<LedgerEventEntity>>

    @Query("SELECT * FROM ledger_events ORDER BY logicalClock DESC, createdAt DESC LIMIT 1")
    suspend fun getLatestLedgerEvent(): LedgerEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgerEvent(event: LedgerEventEntity)

    @Query("SELECT * FROM internal_transfers ORDER BY timestamp DESC")
    fun getAllTransfers(): Flow<List<TransferEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: TransferEventEntity)

    @Query("UPDATE internal_transfers SET status = :status, confirmedBy = :confirmedBy WHERE id = :transferId")
    suspend fun updateTransferStatus(transferId: String, status: String, confirmedBy: String)

    @Query("UPDATE internal_transfers SET relationshipAcknowledgment = :ack WHERE id = :transferId")
    suspend fun updateTransferAcknowledgment(transferId: String, ack: String)
}
