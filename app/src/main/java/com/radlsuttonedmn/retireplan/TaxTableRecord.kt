package com.radlsuttonedmn.retireplan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="TaxTable")
data class TaxTableRecord(
        val rangeBottom: Double,
        val rangeTop: Double,
        val single: Double,
        val marriedJointly: Double,
        val marriedSeparate: Double,
        val household: Double,
        @PrimaryKey(autoGenerate = true) val id: Long = 0)