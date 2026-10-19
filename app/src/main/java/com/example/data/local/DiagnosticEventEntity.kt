package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diagnostic_events")
data class DiagnosticEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val alertType: String,
    val severity: String,
    val titleUrdu: String,
    val messageUrdu: String,
    val englishSummary: String,
    val wasSpoken: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
