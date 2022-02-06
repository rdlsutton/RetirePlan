package com.radlsuttonedmn.retireplan

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [BudgetItem::class, EarningsRecord::class, ExpectancyRecord::class, Individual::class,
    PortfolioRecord::class, TaxTableRecord::class], version = 7, exportSchema = false)
abstract class RetirePlanDatabase : RoomDatabase() {

    abstract fun budgetItemsDao(): BudgetItemsDao?
    abstract fun earningsRecordsDao(): EarningsRecordsDao?
    abstract fun expectanciesRecordsDao(): ExpectanciesRecordsDao?
    abstract fun individualsDao(): IndividualsDao?
    abstract fun portfolioRecordsDao(): PortfolioRecordsDao?
    abstract fun taxTableRecordsDao(): TaxTableRecordsDao?
}
