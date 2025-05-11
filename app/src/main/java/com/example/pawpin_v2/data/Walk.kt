package com.example.pawpin_v2.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date // ← IMPORTANT (not import java.util.*)

@Entity(tableName = "walks")
data class Walk(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Date,
    val distanceMeters: Float,
    val durationSeconds: Long,
    val startLat: Double,
    val startLng: Double,
    val endLat: Double,
    val endLng: Double
)


