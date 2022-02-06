package com.radlsuttonedmn.retireplan

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow

/**
 * Manage the list of portfolio balance items
 */

class PortfolioViewModel(application: Application) : AndroidViewModel(application) {

    private var portfolioList = ArrayList<PortfolioRecord>()

    // Get the list of portfolio balance values from the database
    fun getPortfolioRecords(name: String) {

        val portfolioValues: List<PortfolioRecord> =
            MainActivity.retirePlanDatabase?.portfolioRecordsDao()?.getPortfolioRecords(name)!!
        portfolioList = portfolioValues.toArrayList()
    }

    // Return the number of portfolio balance records in the list
    fun getCount(): Int {
        return portfolioList.size
    }

    // Get the key value for the specified portfolio record
    fun getKey(index: Int?): Long {
        return if (index == null) {
            0
        } else {
            portfolioList[index].id
        }
    }

    // Get the month value from the specified portfolio record
    fun getMonth(index: Int?): String {
        return if (index == null) {
            ""
        } else {
            getMonthName(portfolioList[index].month)
        }
    }

    // Get the month value from the specified portfolio record
    fun getMonthNumber(index: Int?): Int {
        return if (index == null) {
            0
        } else {
            portfolioList[index].month
        }
    }

    // Get the year value from the specified portfolio record
    fun getYear(index: Int?): Int {
        return if (index == null) {
            0
        } else {
            portfolioList[index].year
        }
    }

    // Get the balance value from the specified portfolio record
    fun getBalance(index: Int?): Double {
        return if (index == null) {
            0.0
        } else {
            portfolioList[index].balance
        }
    }

    // Check for duplicate month and year values
    fun isDuplicate(name: String, month: String, year: Int): Boolean {

        var indexFound  = false
        for (portfolioRecord in portfolioList) {
            if (portfolioRecord.name == name && portfolioRecord.month == getMonthNumber(month) && portfolioRecord.year == year) {
                indexFound = true
            }
        }
        return indexFound
    }

    // Check if a portfolio balance edit has produced a duplicate month and year
    fun isDuplicateEdit(key: Long, name: String, month: String, year: Int): Boolean {

        var indexFound  = false
        for (portfolioRecord in portfolioList) {
            if (portfolioRecord.name == name && portfolioRecord.month == getMonthNumber(month) &&
                        portfolioRecord.year == year && portfolioRecord.id != key) {
                indexFound = true
            }
        }
        return indexFound
    }

    // Add a new portfolio record to the database
    fun addRecord(name: String, month: String, year: Int, balance: Double) {

        MainActivity.retirePlanDatabase?.portfolioRecordsDao()?.
                addPortfolioRecord(PortfolioRecord(name, getMonthNumber(month), year, balance))!!
    }

    // Update an existing portfolio record in the database
    fun updateRecord(key: Long, name: String, month: String, year: Int, balance: Double) {

        MainActivity.retirePlanDatabase?.portfolioRecordsDao()?.
                updatePortfolioRecord(PortfolioRecord(name, getMonthNumber(month), year, balance, key))!!
    }

    // Remove a portfolio record from the database
    fun deleteRecord(index: Int) {

        MainActivity.retirePlanDatabase?.portfolioRecordsDao()?.
                deletePortfolioRecord(portfolioList[index])!!
        portfolioList.removeAt(index)
    }

    // Utility function to convert a list to an array list
    private fun <T> List<T>.toArrayList(): ArrayList<T>{
        return ArrayList(this)
    }

    // Get the growth rate from the shared preferences
    fun getGrowthRate(context: Context, name: String): String {

        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("GrowthRate$name", "")!!
    }

    // Get the monthly withdrawal from the shared preferences
    fun getMonthlyWithdrawal(context: Context, name: String): String {

        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("MonthlyWithdrawal$name", "")!!
    }

    // Save the portfolio growth rate to the shared preferences
    fun saveGrowthRate(context: Context, growthRate: String, name: String) {

        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("GrowthRate$name", growthRate)
        editor.apply()
    }

    // Save the portfolio monthly withdrawal to the shared preferences
    fun saveMonthlyWithdrawal(context: Context, monthlyWithdrawal: String, name: String) {

        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("MonthlyWithdrawal$name", monthlyWithdrawal)
        editor.apply()
    }

    // Compute the overall gain from the item list month and year to the beginning of the portfolio
    fun getOverallGains(): ArrayList<OverallGainItem> {

        // Initialize the list of overall gains
        val overallGainsList = ArrayList<OverallGainItem>()

        // Bail out if there are no stored portfolio values
        if (portfolioList.size == 0) {
            return overallGainsList
        }

        // Set up the required variables
        var numberOfFullYears: Int
        var numberOfMonths: Int
        var numberOfYears: Double
        var overallGain: Double

        // Get the initial month and year values from the portfolio list
        val initialMonth = portfolioList[0].month
        val initialYear = portfolioList[0].year
        val initialBalance = portfolioList[0].balance

        // Compute the gain for each month and year in the list
        for (portfolioItem in portfolioList) {

            // For the initial month and year set the gain to 0
            if (portfolioItem.month == initialMonth && portfolioItem.year == initialYear) {
                overallGainsList.add(OverallGainItem(getMonthName(portfolioItem.month), portfolioItem.year, 0.0))
            } else {

                // Determine the number of months between list items date and the initial date
                if (portfolioItem.year == initialYear) {
                    numberOfMonths = portfolioItem.month - initialMonth
                } else {
                    numberOfMonths = 12 - initialMonth
                    numberOfFullYears = portfolioItem.year - initialYear - 1
                    numberOfMonths += 12 * numberOfFullYears
                    numberOfMonths += portfolioItem.month - 1
                }

                // Convert the number of months into the number of years
                numberOfYears = numberOfMonths * 0.0833

                // Compute the overall gain
                overallGain = ((portfolioItem.balance / initialBalance).pow((1 / numberOfYears)) - 1) * 100.0
                overallGainsList.add(OverallGainItem(getMonthName(portfolioItem.month), portfolioItem.year, overallGain))
            }
        }

        return overallGainsList
    }

    // Compute the gain over the defined period to the portfolio to the present time
    fun getPeriodGains(periodSetting: PeriodSetting): ArrayList<PeriodGainItem> {

        // Initialize the list of overall gains
        val periodGainsList = ArrayList<PeriodGainItem>()

        // Bail out if there are no stored portfolio values
        if (portfolioList.size == 0) {
            return periodGainsList
        }

        // Convert the period setting to number of months
        val periodLength = when (periodSetting) {
            PeriodSetting.SIX_MONTH -> 6
            PeriodSetting.ONE_YEAR -> 12
            PeriodSetting.TWO_YEAR -> 24
            PeriodSetting.FIVE_YEAR -> 60
            PeriodSetting.TEN_YEAR -> 120
            PeriodSetting.TWENTY_YEAR -> 240
        }

        // Initialize the variables for the balance list creation
        val balancesList = ArrayList<Double>()
        var firstOne = true
        var lastMonth = 0
        var lastYear = 0
        var balanceEntered: Boolean
        var loopCount = 0

        // Create a list of balances with any missing entries filled with zero
        for (portfolioItem in portfolioList) {

            if (firstOne) {

                // Start the balances list with the first record and initialize the running month and year values
                balancesList.add(portfolioItem.balance)
                firstOne = false
                lastMonth = portfolioItem.month
                lastYear = portfolioItem.year
            } else {

                // Loop through the portfolio values, fill any missing months with zero
                balanceEntered = false
                while (!balanceEntered && loopCount < 10000) {
                    if (portfolioItem.year == lastYear) {

                        // If the new month is the next month in the same year, the balance can be entered
                        if (portfolioItem.month == lastMonth + 1) {
                            balancesList.add(portfolioItem.balance)
                            balanceEntered = true
                        } else {
                            balancesList.add(0.0)
                        }
                    } else {

                        // If the values are crossing from December of last year to January of this year balance can be entered
                        if (lastMonth == 11 && portfolioItem.month == 0 && portfolioItem.year == lastYear + 1) {
                            balancesList.add(portfolioItem.balance)
                            balanceEntered = true
                        } else {
                            balancesList.add(0.0)
                        }
                    }

                    // Update the running year and month values, with month going back to zero if crossing into a new year
                    lastMonth++
                    if (lastMonth == 12) {
                        lastMonth = 0
                        lastYear++
                    }

                    // Use the loop count to prevent an infinite loop
                    loopCount++
                }
            }
        }

        // Report an infinite loop error if the loop limit is reached
        if (loopCount >= 10000) {
            return periodGainsList
        }

        // Initialize the period gain values
        var gainPercent: Double
        var numberOfYears: Double

        // Fill the list of period gain values
        for (portfolioItem in portfolioList) {

            val balanceIndex = balancesList.indexOf(portfolioItem.balance)

            // If the gain period extends beyond the beginning of the portfolio list report 0 gain
            if (balanceIndex - periodLength < 1) {
                periodGainsList.add(PeriodGainItem(getMonthName(portfolioItem.month),
                        portfolioItem.year, 0.0))
            } else {

                // For zero balance entries in the balances list, report 0 gain
                if (balancesList[balanceIndex - periodLength] < 0.1) {
                    periodGainsList.add(PeriodGainItem(getMonthName(portfolioItem.month),
                        portfolioItem.year, 0.0))
                } else {

                    // Compute the gain values and add a new entry to the period gains list
                    numberOfYears = periodLength * 0.0833
                    gainPercent = ((portfolioItem.balance / balancesList[balanceIndex - periodLength]).
                            pow((1 / numberOfYears)) - 1) * 100.0
                    periodGainsList.add(PeriodGainItem(getMonthName(portfolioItem.month),
                        portfolioItem.year, gainPercent))
                }
            }
        }
        return periodGainsList
    }

    // Compute the portfolio end date
    fun getPortfolioEndDate(context: Context?, name: String): String {

        // If the portfolio data is not available, bail out on the calculation
        if (portfolioList.size == 0) {
            return ""
        }

        // Get the portfolio settings values
        val growthRate = getGrowthRate(context!!, name)
        val monthlyWithdrawal = getMonthlyWithdrawal(context, name)

        // If the settings data is not available, bail out on the calculation
        if (growthRate == "" || monthlyWithdrawal == "") {
            return ""
        }

        // Get the current month and year
        val c = Calendar.getInstance()
        val currentYear = c.get(Calendar.YEAR)
        val currentMonth = c.get(Calendar.MONTH)

        // Get the most recent balance value
        val currentBalance = portfolioList[portfolioList.size - 1].balance

        // Convert string settings values to doubles
        val rate = growthRate.toDouble() / 100
        val withdrawal = monthlyWithdrawal.toDouble()

       // Determine if withdrawal amount is greater than portfolio growth amount
       if (currentBalance * (rate/12) > withdrawal) {
           return "Forever"
       }

        // Determine the number of months before portfolio goes to zero
        var remainingBalance = currentBalance
        var monthCount = 0
        while (remainingBalance > 0.0) {
            remainingBalance -= withdrawal
            if (remainingBalance > 0.0) {
                remainingBalance *= (1 + (rate / 12))
            }
            monthCount++
        }

        // Determine the end month and year
        var month = currentMonth
        var year = currentYear

        // Count up the whole years
        while (monthCount >= 12) {
            year++
            monthCount -= 12
        }

        // Update the month value
        month += monthCount
        if (month >= 12) {
            year++
            month -= 12
        }
        return "${getMonthName(month)} $year"
    }

    // Utility function to return a month number for a specified month string
    private fun getMonthNumber(month: String): Int {

        return  when (month) {
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
    }

    // Utility function to return a month name for a specified month number
    private fun getMonthName(month: Int): String {

        return  when (month) {
            1 -> "February"
            2 -> "March"
            3 -> "April"
            4 -> "May"
            5 -> "June"
            6 -> "July"
            7 -> "August"
            8 -> "September"
            9 -> "October"
            10 -> "November"
            11 -> "December"
            else -> "January"
        }
    }
}
