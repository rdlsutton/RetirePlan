package com.radlsuttonedmn.retireplan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="PortfolioRecords")
data class PortfolioRecord(
        val name: String,
        val month: Int,
        val year: Int,
        val balance: Double,
        @PrimaryKey(autoGenerate = true) val id: Long = 0)
