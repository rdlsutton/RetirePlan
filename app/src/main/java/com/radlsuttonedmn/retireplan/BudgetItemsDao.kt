package com.radlsuttonedmn.retireplan

import androidx.room.*

@Dao
interface BudgetItemsDao {

    @Query("select * from Budget where name = :name order by id")
    fun getBudgetItems(name: String): List<BudgetItem>

    @Insert
    fun addBudgetItem(budgetItem: BudgetItem)

    @Update
    fun updateBudgetItem(budgetItem: BudgetItem)

    @Delete
    fun deleteBudgetItem(budgetItem: BudgetItem)

    @Query("delete from Budget where name = :name")
    fun deleteAllBudget(name: String)
}
