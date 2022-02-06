package com.radlsuttonedmn.retireplan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlin.collections.ArrayList

/**
 * Manage the list of budget items
 */

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private var budgetList = ArrayList<BudgetItem>()

    // Get the list of budget items from the database
    fun getBudgetItems(name: String) {

        val budgetValues: List<BudgetItem> =
                MainActivity.retirePlanDatabase?.budgetItemsDao()?.getBudgetItems(name)!!
        budgetList = budgetValues.toArrayList()
    }

    // Return the number of budget items in the list
    fun getCount(): Int {
        return budgetList.size
    }

    // Get the key value for the specified budget record
    fun getKey(index: Int?): Long {
        return if (index == null) {
            0
        } else {
            budgetList[index].id
        }
    }

    // Get the category for the specified budget item
    fun getCategory(index: Int?): String {
        return if (index == null) {
            ""
        } else {
            budgetList[index].category
        }
    }

    // Get the amount value for the specified budget item
    fun getAmount(index: Int?): Double {
        return if (index == null) {
            0.0
        } else {
            budgetList[index].amount
        }
    }

    // Check for duplicate category values
    fun isDuplicate(name: String, category: String): Boolean {

        var indexFound  = false
        for (budgetRecord in budgetList) {
            if (budgetRecord.name == name && budgetRecord.category == category) {
                indexFound = true
            }
        }
        return indexFound
    }

    // Check if a budget item edit has produced a duplicate category
    fun isDuplicateEdit(key: Long, name: String, category: String): Boolean {

        var indexFound  = false
        for (budgetRecord in budgetList) {
            if (budgetRecord.name == name && budgetRecord.category == category && budgetRecord.id != key) {
                indexFound = true
            }
        }
        return indexFound
    }

    // Add a new budget item to the database
    fun addBudgetItem(name: String, category: String, amount: Double) {

        MainActivity.retirePlanDatabase?.budgetItemsDao()?.
            addBudgetItem(BudgetItem(name, category, amount))!!
    }

    // Update an existing budget item in the database
    fun updateBudgetItem(key: Long, name: String, category: String, amount: Double) {

        MainActivity.retirePlanDatabase?.budgetItemsDao()?.
            updateBudgetItem(BudgetItem(name, category, amount, key))!!
    }

    // Remove a budget item from the database
    fun deleteBudgetItem(index: Int) {

        MainActivity.retirePlanDatabase?.budgetItemsDao()?.
            deleteBudgetItem(budgetList[index])!!
        budgetList.removeAt(index)
    }

    // Utility function to convert a list to an array list
    private fun <T> List<T>.toArrayList(): ArrayList<T>{
        return ArrayList(this)
    }
}
