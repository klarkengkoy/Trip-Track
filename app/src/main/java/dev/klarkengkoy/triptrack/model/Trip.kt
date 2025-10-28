package dev.klarkengkoy.triptrack.model

import java.time.LocalDate
import java.util.UUID

data class Trip(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val currency: String,
    val imageUri: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val dailyBudget: Double? = null,
    val totalBudget: Double? = null,
    val isActive: Boolean = false
)
