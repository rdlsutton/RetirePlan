package com.radlsuttonedmn.retireplan

import androidx.room.*

@Dao
interface TaxTableRecordsDao {

    @Query("select * from TaxTable order by rangeBottom")
    fun getTaxTableRecords(): List<TaxTableRecord>

    @Insert
    fun addTaxTableRecord(taxTableRecord: TaxTableRecord)

    @Query("delete from TaxTable")
    fun deleteTaxTableRecords()

    @Query("delete from TaxTable")
    fun deleteAllTaxTable()
}