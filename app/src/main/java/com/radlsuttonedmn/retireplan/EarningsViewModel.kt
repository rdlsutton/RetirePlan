package com.radlsuttonedmn.retireplan

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import java.util.*
import kotlin.collections.ArrayList

/**
 * Manage the list of earnings items
 */

class EarningsViewModel(application: Application) : AndroidViewModel(application) {

    private var earningsList = ArrayList<EarningsRecord>()
    private var adjustedEarnings = ArrayList<Double>()

    // Get the list of earnings values from the database
    fun getEarnings(context: Context, name: String) {

        // Get the earnings from the database
        val earningsValues: List<EarningsRecord> =
                MainActivity.retirePlanDatabase?.earningsRecordsDao()?.getEarningsRecords(name)!!
        earningsList = earningsValues.toArrayList()
        calculateAdjustedEarnings(context, name)
    }

    // Get the filing status from the shared preferences
    fun getFilingStatus(context: Context, name: String): String {

        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("FilingStatus$name", "")!!
    }

    // Get the year turned 60 value from the shared preferences
    fun getAge60Year(context: Context, name: String): String {

        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("Age60Year$name", "")!!
    }

    // Get the start month from the shared preferences
    fun getStartMonth(context: Context, name: String): String {

        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("StartMonth$name", "")!!
    }

    // Get the start month from the shared preferences
    fun getStartYear(context: Context, name: String): String {

        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("StartYear$name", "")!!
    }

    // Calculate the adjusted earnings for each year entry
    private fun calculateAdjustedEarnings(context: Context, name: String) {

        // Get the year turned age 60 value
        val age60YearSetting = getAge60Year(context, name)
        var age60Year = 0
        if (age60YearSetting != "") {
            age60Year = age60YearSetting.toInt()
        }

        // Get the current year
        val c = Calendar.getInstance()
        val currentYear = c.get(Calendar.YEAR)

        // Get the average earnings value for the base year if it exists
        var baseEarnings = 0.0

        if (age60Year <= currentYear) {
            for (earnings in earningsList) {
                if (earnings.year == age60Year) {
                    baseEarnings = earnings.averageEarnings
                }
            }
        }

        // If the base year was not found, set the adjusted earnings values to the reported earnings
        if (baseEarnings == 0.0) {
            for (earnings in earningsList) {
                adjustedEarnings.add(earnings.reportedEarnings)
            }
            return
        }

        // Calculate the adjusted earnings value for each year entry
        for (earnings in earningsList) {

            // For years after the base year adjusted earnings = reported earnings
            if (earnings.year > 2017) {
                adjustedEarnings.add(earnings.reportedEarnings)
            } else {
                if (earnings.averageEarnings > 0.0 && earnings.reportedEarnings > 0.0) {
                    adjustedEarnings.add((baseEarnings / earnings.averageEarnings) * earnings.reportedEarnings)
                } else {
                    adjustedEarnings.add(0.0)
                }
            }
        }
    }

    // Return the number of earnings records in the list
    fun getCount(): Int {
        return earningsList.size
    }

    // Get the key value for the specified earnings record
    fun getKey(index: Int?): Long {
        return if (index == null) {
            0
        } else {
            earningsList[index].id
        }
    }

    // Get the year value from the specified earnings record
    fun getYear(index: Int?): Int {
        return if (index == null) {
            0
        } else {
            earningsList[index].year
        }
    }

    // Get the reported earnings value from the specified earnings record
    fun getReportedEarnings(index: Int?): Double {
        return if (index == null) {
            0.0
        } else {
            earningsList[index].reportedEarnings
        }
    }

    // Get the average earnings value from the specified earnings record
    fun getAverageEarnings(index: Int?): Double {
        return if (index == null) {
            0.0
        } else {
            earningsList[index].averageEarnings
        }
    }

    // Get the adjusted earnings value from the specified earnings record
    fun getAdjustedEarnings(index: Int?): Double {
        return if (index == null) {
            0.0
        } else {
            adjustedEarnings[index]
        }
    }

    // Check for duplicate month and year values
    fun isDuplicate(name: String, year: Int): Boolean {

        var indexFound  = false
        for (earnings in earningsList) {
            if (earnings.name == name && earnings.year == year) {
                indexFound = true
            }
        }
        return indexFound
    }

    // Check if a portfolio balance edit has produced a duplicate month and year
    fun isDuplicateEdit(key: Long, name: String, year: Int): Boolean {

        var indexFound  = false
        for (earnings in earningsList) {
            if (earnings.name == name && earnings.year == year && earnings.id != key) {
                indexFound = true
            }
        }
        return indexFound
    }

    // Add a new earnings record to the database
    fun addEarnings(name: String, year: Int, reportedEarnings: Double, averageEarnings: Double) {

        MainActivity.retirePlanDatabase?.earningsRecordsDao()?.
            addEarningsRecord(EarningsRecord(name, year, reportedEarnings, averageEarnings))!!
    }

    // Update an existing earnings record in the database
    fun updateEarnings(key: Long, name: String, year: Int, reportedEarnings: Double, averageEarnings: Double) {

        MainActivity.retirePlanDatabase?.earningsRecordsDao()?.
            updateEarningsRecord(EarningsRecord(name, year, reportedEarnings, averageEarnings, key))!!
    }

    // Remove an earnings record from the database
    fun deleteEarnings(index: Int) {

        MainActivity.retirePlanDatabase?.earningsRecordsDao()?.
            deleteEarningsRecord(earningsList[index])!!
        earningsList.removeAt(index)
        adjustedEarnings.removeAt(index)
    }

    // Save the filing status to the shared preferences
    fun saveFilingStatus(context: Context, filingStatus: String, name: String) {

        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("FilingStatus$name", filingStatus)
        editor.apply()
    }

    // Save the year turned 60 value to the shared preferences
    fun saveAge60Year(context: Context, age60Year: String, name: String) {

        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("Age60Year$name", age60Year)
        editor.apply()
    }

    // Save the social security start date data to the shared preferences
    fun saveStartDate(context: Context, startMonth: String, startYear: String, name: String) {

        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("StartMonth$name", startMonth)
        editor.putString("StartYear$name", startYear)
        editor.apply()
    }

    // Calculate the monthly social security amount for the specified start date
    fun calculateMonthlySocialSecurity(context: Context?, name: String): Double {

        // Get the social security start date
        val startMonth = getStartMonth(context!!, name)
        val prefYear = getStartYear(context, name)

        val startYear: Int = if (prefYear == "") {
            0
        } else {
            prefYear.toInt()
        }

        // If data has not been entered, return zero
        if (earningsList.size == 0) {
            return 0.0
        }
        if (startMonth == "" || startYear == 0) {
            return 0.0
        }

        // Sort the earnings list with the highest value first
        val sortedList = adjustedEarnings.sortedWith(compareByDescending { it })
        var earningsSum = 0.0

        // Add the top 35 adjusted earnings amounts together
        for ((index, earnings) in sortedList.withIndex()) {
            if (index < 35) {
                earningsSum += earnings
            }
        }

        // Compute the full retirement age social security amount
        var fullAmount = 2498.32 + (earningsSum/420 - 6002) * .15

        // Increase fullAmount for the COLA adjustment going from 2021  to 2022
        fullAmount *= 1.059

        // Convert startMonth to number value
        var monthsDifference: Int
        val monthNumber:Int = when (startMonth) {
            "February" -> 1
            "March" -> 2
            "April" -> 3
            "May" -> 4
            "June" -> 5
            "July" -> 6
            "August" -> 7
            "September" -> 8
            "October" -> 9
            "November" -> 10
            "December" -> 11
            else -> 0
        }

        // Determine the difference in months between start date and full retirement date
        var adjustedAmount = fullAmount

        if (startYear < 2024 || (startYear == 2024 && monthNumber < 2)) {

            // Start date is prior to full retirement date
            if (startYear == 2024) {
                monthsDifference = 2 - monthNumber
            } else {

                // To determine months difference add 12 months for each whole year
                monthsDifference = (2024 - startYear - 1) * 12

                // Add the months in the partial year for the start year
                monthsDifference += 12 - monthNumber - 1

                // Add the months that are in 2024
                monthsDifference += 3
            }

            // Adjust the social security payment for the current start date
            adjustedAmount = fullAmount - ((fullAmount * .00555) * monthsDifference)
        }

        if (startYear > 2024 || (startYear == 2024 && monthNumber > 2)) {

            // Start date is after the full retirement date
            if (startYear == 2024) {
                monthsDifference = monthNumber - 2
            } else {
                // To determine months difference add 12 months for each whole year
                monthsDifference = (startYear - 2024 - 1) * 12

                // Add the months in the partial year for the start year
                monthsDifference += monthNumber + 1

                // Add the months that are in 2024
                monthsDifference += 9
            }

            // Adjust the social security payment for the current start date
            adjustedAmount = fullAmount + ((fullAmount * .0041667) * monthsDifference)
        }
        return adjustedAmount
    }

    fun calculateAfterTaxMonthly(context: Context, monthlyIRA: Double, monthlySSI: Double, name: String): Double {

        // If the needed data is not available bail out
        if (monthlyIRA < 1 || monthlySSI < 1) {
            return 0.0
        }

        // Get the tax table from the database
        val taxTableEntries: List<TaxTableRecord> =
                MainActivity.retirePlanDatabase?.taxTableRecordsDao()?.getTaxTableRecords()!!
        val taxTableList = taxTableEntries.toArrayList()

        // If we do not have the tax table bail out
        if (taxTableList.size == 0) {
            return 0.0
        }

        // Calculate total IRA distribution (1040 line 4)
        val totalIRADistribution = monthlyIRA * 12

        // Calculate total SSI benefits (SSI worksheet line 1)
        val totalSSI = monthlySSI * 12

        // Calculate SSI worksheet line 2
        val line2 = totalSSI * 0.50

        // Calculate SSI worksheet line 3
        val line3 = totalIRADistribution

        // Calculate SSI worksheet line 7
        val line7 = line2 + line3

        // Calculate SSI worksheet line 8
        val line8 = 25000.00

        // Calculate SSI worksheet line 9
        val line9 = line7 - line8

        // Calculate SSI worksheet line10
        val line10 = 9000.00

        // Calculate SSI worksheet line 11
        var line11 = line9 - line10
        if (line11 < 0.0) {
            line11 = 0.0
        }

        // Calculate SSI worksheet line 12
        val line12: Double = if (line10 < line9) {
            line10
        } else {
            line9
        }

        // Calculate SSI worksheet line 13
        val line13 = line12 * 0.5

        // Calculate SSI worksheet line 14
        val line14: Double = if (line2 < line13) {
            line2
        } else {
            line13
        }

        // Calculate SSI worksheet line 15
        val line15 = line11 * 0.85

        // Calculate SSI worksheet line 16
        val line16 = line14 + line15

        // Calculate SSI worksheet line 17
        val line17 = totalSSI * 0.85

        // Calculate the taxable SSI (1040 line 6b)
        val taxableSSI: Double = if (line16 < line17) {
            line16
        } else {
            line17
        }

        // Calculate the total income
        val totalTaxableIncome = taxableSSI + totalIRADistribution

        // Compute adjusted gross income
        val adjustedGrossIncome = totalTaxableIncome - 14050.00

        // If the adjustedGrossIncome is not in the tax table range bail out
        if (adjustedGrossIncome < taxTableList[0].rangeBottom) {
            return 0.0
        }
        if (adjustedGrossIncome > taxTableList[taxTableList.size - 1].rangeBottom) {
            return 0.0
        }

        // Compute the annual tax according to tax table
        var annualTax = 0.0
        val filingStatus = getFilingStatus(context, name)

        for (taxEntry in taxTableList) {
            if (adjustedGrossIncome >= taxEntry.rangeBottom) {
                annualTax = when (filingStatus) {
                    "Single" -> taxEntry.single
                    "Married Joint" -> taxEntry.marriedJointly
                    "Married Separate" -> taxEntry.marriedSeparate
                    "Household" -> taxEntry.household
                    else -> 0.0
                }
            }
        }

        // Calculate the total monthly income
        return (totalIRADistribution + totalSSI - annualTax)/12
    }

    // Utility function to convert a list to an array list
    private fun <T> List<T>.toArrayList(): ArrayList<T>{
        return ArrayList(this)
    }
}
