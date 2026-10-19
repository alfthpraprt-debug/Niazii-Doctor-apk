package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cleanup_history")
data class CacheCleanupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val mode: String,
    val bytesCleaned: Long,
    val freeSpaceBefore: Long,
    val freeSpaceAfter: Long,
    val success: Boolean,
    val notesUrdu: String,
    val timestamp: Long = System.currentTimeMillis()
)
