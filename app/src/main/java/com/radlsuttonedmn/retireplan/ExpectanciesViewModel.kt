package com.radlsuttonedmn.retireplan

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/**
 * Manage the list of life expectancy items
 */

class ExpectanciesViewModel(application: Application) : AndroidViewModel(application) {

    private var expectanciesList = ArrayList<ExpectancyRecord>()

    // Get the list of life expectancy values from the database
    fun getExpectancies() {

        val expectanciesValues: List<ExpectancyRecord> =
                MainActivity.retirePlanDatabase?.expectanciesRecordsDao()?.getExpectanciesRecords()!!
        expectanciesList = expectanciesValues.toArrayList()
    }

    // Return the number of life expectancy records in the list
    fun getCount(): Int {
        return expectanciesList.size
    }

    // Get the key value for the specified life expectancy record
    fun getKey(index: Int?): Long {
        return if (index == null) {
            0
        } else {
            expectanciesList[index].id
        }
    }

    // Get the age value from the specified life expectancy record
    fun getAge(index: Int?): Int {
        return if (index == null) {
            0
        } else {
            expectanciesList[index].age
        }
    }

    // Get the sex value from the specified life expectancy record
    fun getSex(index: Int?): String {
        return if (index == null) {
            ""
        } else {
            expectanciesList[index].sex
        }
    }

    // Get the 75% probability value from the specified life expectancy record
    fun getProbability75(index: Int?): Int {
        return if (index == null) {
            0
        } else {
            expectanciesList[index].probability75
        }
    }

    // Get the 75% probability value from the specified life expectancy record
    fun getProbability50(index: Int?): Int {
        return if (index == null) {
            0
        } else {
            expectanciesList[index].probability50
        }
    }

    // Get the 75% probability value from the specified life expectancy record
    fun getProbability25(index: Int?): Int {
        return if (index == null) {
            0
        } else {
            expectanciesList[index].probability25
        }
    }

    // Check for duplicate age and sex values
    fun isDuplicate(age: Int, sex: String): Boolean {

        var indexFound  = false
        for (expectancy in expectanciesList) {
            if (expectancy.age == age && expectancy.sex == sex) {
                indexFound = true
            }
        }
        return indexFound
    }

    // Check if a portfolio balance edit has produced a duplicate month and year
    fun isDuplicateEdit(key: Long, age: Int, sex: String): Boolean {

        var indexFound  = false
        for (expectancy in expectanciesList) {
            if (expectancy.age == age && expectancy.sex == sex && expectancy.id != key) {
                indexFound = true
            }
        }
        return indexFound
    }

    // Add a new life expectancy record to the database
    fun addExpectancy(age: Int, sex: String, probability75: Int, probability50: Int, probability25: Int) {

        MainActivity.retirePlanDatabase?.expectanciesRecordsDao()?.
        addExpectancyRecord(ExpectancyRecord(age, sex, probability75, probability50, probability25))!!
    }

    // Update an existing life expectancy record in the database
    fun updateExpectancy(key: Long, age: Int, sex: String, probability75: Int, probability50: Int, probability25: Int) {

        MainActivity.retirePlanDatabase?.expectanciesRecordsDao()?.
        updateExpectancyRecord(ExpectancyRecord(age, sex, probability75, probability50, probability25, key))!!
    }

    // Remove a life expectancy record from the database
    fun deleteExpectancy(index: Int) {

        MainActivity.retirePlanDatabase?.expectanciesRecordsDao()?.
        deleteExpectancyRecord(expectanciesList[index])!!
        expectanciesList.removeAt(index)
    }

    // Utility function to convert a list to an array list
    private fun <T> List<T>.toArrayList(): ArrayList<T>{
        return ArrayList(this)
    }
}