package com.radlsuttonedmn.retireplan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="ExpectanciesRecords")
data class ExpectancyRecord(
        val age: Int,
        val sex: String,
        val probability75: Int,
        val probability50: Int,
        val probability25: Int,
        @PrimaryKey(autoGenerate = true) val id: Long = 0)