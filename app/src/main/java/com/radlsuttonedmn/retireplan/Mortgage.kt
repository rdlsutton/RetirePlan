package com.radlsuttonedmn.retireplan

import android.content.Context
import java.util.*

/**
 * Collection the parameters for mortgage calculations
 */

class Mortgage(context: Context, name: String) {

    var balance = 0.0
    var interestRate = 0.0
    var principalInterest = 0.0
    var houseValue = 0.0

    // Retrieve the mortgage values from the shared preferences
    init {

        // Get the mortgage values from the shared preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val savedBalance = preferences.getString("MortgageBalance$name", "")
        val savedInterestRate = preferences.getString("MortgageInterestRate$name", "")
        val savedPrincipalInterest = preferences.getString("MortgagePrincipalInterest$name", "")
        val savedHouseValue = preferences.getString("MortgageHouseValue$name", "")

        balance = if (savedBalance == null || savedBalance == "") {
            0.0
        } else {
            savedBalance.toDouble()
        }
        interestRate = if (savedInterestRate == null || savedInterestRate == "") {
            0.0
        } else {
            savedInterestRate.toDouble()
        }
        principalInterest = if (savedPrincipalInterest == null || savedPrincipalInterest == "") {
            0.0
        } else {
            savedPrincipalInterest.toDouble()
        }
        houseValue = if (savedHouseValue == null || savedHouseValue == "") {
            0.0
        } else {
            savedHouseValue.toDouble()
        }
    }

    // Save the mortgage values to a JSON file
    fun saveMortgage(context: Context, name: String) {

        // Save the mortgage data in the shared preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("MortgageBalance$name", balance.toString())
        editor.putString("MortgageInterestRate$name", interestRate.toString())
        editor.putString("MortgagePrincipalInterest$name", principalInterest.toString())
        editor.putString("MortgageHouseValue$name", houseValue.toString())
        editor.apply()
    }

    // Determine the date when the mortgage will be payed off
    fun calculatePayoffDate(): String {

        // Get the mortgage data
        var remainingBalance = balance

        // If the needed mortgage data is not available then set the return to blank
        if (remainingBalance == 0.0 || interestRate == 0.0 || principalInterest == 0.0) {
            return ""
        }

        // Get the current month and year
        val calendar = Calendar.getInstance()
        var month = calendar.get(Calendar.MONTH)
        var year = calendar.get(Calendar.YEAR)

        // Calculate the expected payoff date
        if (remainingBalance != 0.0 && interestRate != 0.0 && principalInterest != 0.0) {

            // If the payment does not cover the interest, bail out
            if ((remainingBalance * interestRate/100)/12 >= principalInterest) {
                return ""
            }

            while (remainingBalance > 0.0) {

                // Calculate remaining balance
                remainingBalance -= (principalInterest - (remainingBalance * interestRate/100)/12)
                month++
                if (month > 11) {
                    month = 0
                    year++
                }
            }
        }

        // Convert the month number to a month name string
        val months = arrayOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )
        val displayMonth = months[month]
        return "$displayMonth $year"
    }

    // Determine the monthly payoff amount for the mortgage
    fun calculatePayoffAmount(): Double {

        var monthlyPayoff = 0.0

        // If the needed mortgage data is not available then set the return to blank
        if (balance == 0.0 || interestRate == 0.0 || principalInterest == 0.0) {
            return 0.0
        }

        // Calculate the monthly payoff amount
        if (balance != 0.0 && interestRate != 0.0 && principalInterest != 0.0) {

            // If the payment does not cover the interest, bail out
            if ((balance * interestRate/100)/12 >= principalInterest) {
                return 0.0
            }

            // Calculate monthly payoff
            monthlyPayoff = (principalInterest - (balance * interestRate/100)/12)
        }

        return monthlyPayoff
    }

    // Determine the equity amount received when house is sold
    fun calculateEquity(): Double {

        var equity = 0.0

        // Bail out on the calculation in the required information is not available
        if (balance != 0.0 && houseValue != 0.0) {

            // Return zero if values have not been entered
            if (houseValue > 0.0) {

                // Calculate real estate agent commission
                val commission = houseValue * .065

                // Calculate deed tax
                val deedTax = houseValue * .0033

                // Calculate fixed fees
                val fixedFees = 1106.0

                // Calculate equity value
                equity = houseValue - balance - commission - deedTax - fixedFees
            }
        }

        return equity
    }
}
