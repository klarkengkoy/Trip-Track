package dev.klarkengkoy.triptrack.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val currency: String,
    val imageUri: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val dailyBudget: Double? = null,
    val totalBudget: Double? = null,
    val isActive: Boolean = false,
    val isDeleted: Boolean = false
)
