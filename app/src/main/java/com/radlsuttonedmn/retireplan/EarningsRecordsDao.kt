package com.radlsuttonedmn.retireplan

import androidx.room.*

@Dao
interface EarningsRecordsDao {

    @Query("select * from EarningsRecords where name = :name order by year")
    fun getEarningsRecords(name: String): List<EarningsRecord>

    @Insert
    fun addEarningsRecord(earningsRecord: EarningsRecord)

    @Update
    fun updateEarningsRecord(earningsRecord: EarningsRecord)

    @Delete
    fun deleteEarningsRecord(earningsRecord: EarningsRecord)

    @Query("delete from EarningsRecords where name = :name")
    fun deleteAllEarnings(name: String)
}