package com.radlsuttonedmn.retireplan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="Budget")
data class BudgetItem(
        val name: String,
        val category: String,
        val amount: Double,
        @PrimaryKey(autoGenerate = true) val id: Long = 0)
