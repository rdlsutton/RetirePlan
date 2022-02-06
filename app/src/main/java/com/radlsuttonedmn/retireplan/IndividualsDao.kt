package com.radlsuttonedmn.retireplan

import androidx.room.*

@Dao
interface IndividualsDao {

    @Query("select * from Individuals order by name")
    fun getNames(): List<Individual>

    @Query("select exists(select * from Individuals WHERE name = :checkName)")
    fun nameExists(checkName : String) : Boolean

    @Insert
    fun addName(individual: Individual)

    @Delete
    fun deleteName(individual: Individual)
}
