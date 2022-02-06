package com.radlsuttonedmn.retireplan

import androidx.room.*

@Dao
interface PortfolioRecordsDao {

    @Query("select * from PortfolioRecords where name = :name order by year, month")
    fun getPortfolioRecords(name: String): List<PortfolioRecord>

    @Insert
    fun addPortfolioRecord(portfolioRecord: PortfolioRecord)

    @Update
    fun updatePortfolioRecord(portfolioRecord: PortfolioRecord)

    @Delete
    fun deletePortfolioRecord(portfolioRecord: PortfolioRecord)

    @Query("delete from PortfolioRecords where name = :name")
    fun deleteAllPortfolio(name: String)
}
