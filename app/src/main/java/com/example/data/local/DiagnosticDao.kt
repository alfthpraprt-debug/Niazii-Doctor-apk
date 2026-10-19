package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosticDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: DiagnosticEventEntity): Long

    @Query("SELECT * FROM diagnostic_events ORDER BY timestamp DESC LIMIT 100")
    fun getAllEvents(): Flow<List<DiagnosticEventEntity>>

    @Query("SELECT * FROM diagnostic_events WHERE wasSpoken = 1 ORDER BY timestamp DESC LIMIT 50")
    fun getSpokenAlertHistory(): Flow<List<DiagnosticEventEntity>>

    @Query("SELECT * FROM diagnostic_events WHERE alertType = :type ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastEventByType(type: String): DiagnosticEventEntity?

    @Query("DELETE FROM diagnostic_events")
    suspend fun clearAll()

    // Cleanup History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCleanup(cleanup: CacheCleanupEntity): Long

    @Query("SELECT * FROM cleanup_history ORDER BY timestamp DESC LIMIT 50")
    fun getCleanupHistory(): Flow<List<CacheCleanupEntity>>

    @Query("DELETE FROM cleanup_history")
    suspend fun clearCleanupHistory()
}
