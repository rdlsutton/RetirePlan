package com.radlsuttonedmn.retireplan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="EarningsRecords")
data class EarningsRecord(
        val name: String,
        val year: Int,
        val reportedEarnings: Double,
        val averageEarnings: Double,
        @PrimaryKey(autoGenerate = true) val id: Long = 0)