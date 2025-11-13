package dev.klarkengkoy.triptrack.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.ui.graphics.vector.ImageVector

sealed class IncomeCategory(val title: String, val icon: ImageVector, val route: String) {
    object Salary : IncomeCategory("Salary", Icons.Default.AccountBalanceWallet, "salary")
    object Gifts : IncomeCategory("Gifts", Icons.Default.Redeem, "gifts")
    object OtherIncome : IncomeCategory("Other Income", Icons.Default.AttachMoney, "other_income")

    companion object {
        fun fromRoute(route: String?): IncomeCategory? {
            return when (route) {
                Salary.route -> Salary
                Gifts.route -> Gifts
                OtherIncome.route -> OtherIncome
                else -> null
            }
        }

        val categories = listOf(
            Salary,
            Gifts,
            OtherIncome
        ).sortedBy { it.title }
    }
}
