package com.radlsuttonedmn.retireplan

import androidx.room.*

@Dao
interface ExpectanciesRecordsDao {

    @Query("select * from ExpectanciesRecords order by sex, age")
    fun getExpectanciesRecords(): List<ExpectancyRecord>

    @Insert
    fun addExpectancyRecord(expectancyRecord: ExpectancyRecord)

    @Update
    fun updateExpectancyRecord(expectancyRecord: ExpectancyRecord)

    @Delete
    fun deleteExpectancyRecord(expectancyRecord: ExpectancyRecord)

    @Query("delete from ExpectanciesRecords")
    fun deleteAllExpectancies()
}