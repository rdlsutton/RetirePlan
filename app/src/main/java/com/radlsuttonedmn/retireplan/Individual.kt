package com.radlsuttonedmn.retireplan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="Individuals")
data class Individual(
        val name: String,
        @PrimaryKey(autoGenerate = true) val id: Long = 0)