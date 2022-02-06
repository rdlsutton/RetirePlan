package com.radlsuttonedmn.retireplan

import android.content.Context
import android.net.Uri
import com.opencsv.CSVReader
import java.io.*


class FileImport {

    // Validate the header row in the selected file
    private fun validHeader(header: Array<String>?, headerSize: Int): Boolean {

        // Do not process a null header
        if (header == null) {
            return false
        }

        // Verify that the number of fields in the header is correct
        if (header.size != headerSize) {
            return false
        }

        return true
    }

    // Import the budget file
    fun importBudget(fileUri: Uri?, context: Context): String {

        var line: Array<String?>?
        var blankLine: Boolean
        var results = context.getString(R.string.import_complete)
        var category: String
        var amount: Double

        // Get the name from the preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val name = preferences.getString("Individual", "")!!

        // Connect to the input file using an InputStreamReader and a CSVReader
        try {
            val inputStream: InputStream = context.contentResolver.openInputStream(fileUri!!)!!
            val inputStreamReader = InputStreamReader(inputStream)
            val csvReader = CSVReader(inputStreamReader)

            // Verify the validity of the header row
            if (validHeader(csvReader.readNext(), 2)) {

                // Clear out all existing earnings data
                MainActivity.retirePlanDatabase?.budgetItemsDao()?.deleteAllBudget(name)!!

                // Read the lines from the input CSV file
                line = csvReader.readNext()
                while (line != null) {

                    // Check for and skip any blank lines
                    blankLine = true
                    for (item in line) {
                        if (item!!.trim { it <= ' ' }.isNotEmpty()) {
                            blankLine = false
                        }
                    }

                    // Process all lines that are not blank
                    if (!blankLine) {
                        category = if (line[0] == null) {
                            ""
                        } else {
                            line[0]!!
                        }
                        amount = if (line[1] == null) {
                            0.0
                        } else {
                            try {
                                line[1]!!.toDouble()
                            } catch (nfe: NumberFormatException) {
                                0.0
                            }
                        }

                        // If the input data is valid write the record to the database
                        if (category != "") {
                            MainActivity.retirePlanDatabase?.budgetItemsDao()?.
                                addBudgetItem(BudgetItem(name, category, amount))!!
                        }
                    }

                    // Read the next input line
                    line = csvReader.readNext()
                }
            } else {
                results = context.getString(R.string.bad_header)
            }
            inputStream.close()
            csvReader.close()

        } catch (e: FileNotFoundException) {
            results = context.getString(R.string.file_not_found)
        } catch (e: IOException) {
            results = context.getString(R.string.file_read_error)
        }
        return results
    }

    // Import the social security earnings file
    fun importSocialSecurity(fileUri: Uri?, context: Context): String {

        var line: Array<String?>?
        var blankLine: Boolean
        var results = context.getString(R.string.import_complete)
        var year: Int
        var reportedEarnings: Double
        var averageEarnings: Double

        // Get the name from the preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val name = preferences.getString("Individual", "")!!

        // Connect to the input file using an InputStreamReader and a CSVReader
        try {
            val inputStream: InputStream = context.contentResolver.openInputStream(fileUri!!)!!
            val inputStreamReader = InputStreamReader(inputStream)
            val csvReader = CSVReader(inputStreamReader)

            // Verify the validity of the header row
            if (validHeader(csvReader.readNext(), 3)) {

                // Clear out all existing earnings data
                MainActivity.retirePlanDatabase?.earningsRecordsDao()?.deleteAllEarnings(name)!!

                // Read the lines from the input CSV file
                line = csvReader.readNext()
                while (line != null) {

                    // Check for and skip any blank lines
                    blankLine = true
                    for (item in line) {
                        if (item!!.trim { it <= ' ' }.isNotEmpty()) {
                            blankLine = false
                        }
                    }

                    // Process all lines that are not blank
                    if (!blankLine) {
                        year = if (line[0] == null) {
                            0
                        } else {
                            try {
                                line[0]!!.toInt()
                            } catch (nfe: NumberFormatException) {
                                0
                            }
                        }
                        reportedEarnings = if (line[1] == null) {
                            0.0
                        } else {
                            try {
                                line[1]!!.toDouble()
                            } catch (nfe: NumberFormatException) {
                                0.0
                            }
                        }
                        averageEarnings = if (line[2] == null) {
                            0.0
                        } else {
                            try {
                                line[2]!!.toDouble()
                            } catch (nfe: NumberFormatException) {
                                0.0
                            }
                        }

                        // If the input data is valid write the record to the database
                        if (year != 0 && (reportedEarnings > 1.0 || averageEarnings > 1.0)) {
                            MainActivity.retirePlanDatabase?.earningsRecordsDao()?.
                                addEarningsRecord(EarningsRecord(name, year, reportedEarnings, averageEarnings))!!
                        }
                    }

                    // Read the next input line
                    line = csvReader.readNext()
                }
            } else {
                results = context.getString(R.string.bad_header)
            }
            inputStream.close()
            csvReader.close()

        } catch (e: FileNotFoundException) {
            results = context.getString(R.string.file_not_found)
        } catch (e: IOException) {
            results = context.getString(R.string.file_read_error)
        }
        return results
    }

    // Import the portfolio file
    fun importPortfolio(fileUri: Uri?, context: Context): String {

        var line: Array<String?>?
        var blankLine: Boolean
        var results = context.getString(R.string.import_complete)
        var month: Int
        var year: Int
        var balance: Double

        // Get the name from the preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val name = preferences.getString("Individual", "")!!

        // Connect to the input file using an InputStreamReader and a CSVReader
        try {
            val inputStream: InputStream = context.contentResolver.openInputStream(fileUri!!)!!
            val inputStreamReader = InputStreamReader(inputStream)
            val csvReader = CSVReader(inputStreamReader)

            // Verify the validity of the header row
            if (validHeader(csvReader.readNext(), 3)) {

                // Clear out all existing portfolio data
                MainActivity.retirePlanDatabase?.portfolioRecordsDao()?.deleteAllPortfolio(name)!!

                // Read the lines from the input CSV file
                line = csvReader.readNext()
                while (line != null) {

                    // Check for and skip any blank lines
                    blankLine = true
                    for (item in line) {
                        if (item!!.trim { it <= ' ' }.isNotEmpty()) {
                            blankLine = false
                        }
                    }

                    // Process all lines that are not blank
                    if (!blankLine) {
                        month = if (line[0] == null) {
                            0
                        } else {
                            try {
                                line[0]!!.toInt()
                            } catch (nfe: NumberFormatException) {
                                0
                            }
                        }
                        year = if (line[1] == null) {
                            0
                        } else {
                            try {
                                line[1]!!.toInt()
                            } catch (nfe: NumberFormatException) {
                                0
                            }
                        }
                        balance = if (line[2] == null) {
                            0.0
                        } else {
                            try {
                                line[2]!!.toDouble()
                            } catch (nfe: NumberFormatException) {
                                0.0
                            }
                        }

                        // If the input data is valid write the record to the database
                        if (year != 0 && balance > 1.0) {
                            MainActivity.retirePlanDatabase?.portfolioRecordsDao()?.
                                addPortfolioRecord(PortfolioRecord(name, month, year, balance))!!
                        }
                    }

                    // Read the next input line
                    line = csvReader.readNext()
                }
            } else {
                results = context.getString(R.string.bad_header)
            }
            inputStream.close()
            csvReader.close()

        } catch (e: FileNotFoundException) {
            results = context.getString(R.string.file_not_found)
        } catch (e: IOException) {
            results = context.getString(R.string.file_read_error)
        }
        return results
    }

    // Import the life expectancies file
    fun importLifeExpectancies(fileUri: Uri?, context: Context): String {

        var line: Array<String?>?
        var blankLine: Boolean
        var results = context.getString(R.string.import_complete)
        var age: Int
        var sex: String
        var probability75: Int
        var probability50: Int
        var probability25: Int

        // Connect to the input file using an InputStreamReader and a CSVReader
        try {
            val inputStream: InputStream = context.contentResolver.openInputStream(fileUri!!)!!
            val inputStreamReader = InputStreamReader(inputStream)
            val csvReader = CSVReader(inputStreamReader)

            // Verify the validity of the header row
            if (validHeader(csvReader.readNext(), 5)) {

                // Clear out all existing life expectancy data
                MainActivity.retirePlanDatabase?.expectanciesRecordsDao()?.deleteAllExpectancies()!!

                // Read the lines from the input CSV file
                line = csvReader.readNext()
                while (line != null) {

                    // Check for and skip any blank lines
                    blankLine = true
                    for (item in line) {
                        if (item!!.trim { it <= ' ' }.isNotEmpty()) {
                            blankLine = false
                        }
                    }

                    // Process all lines that are not blank
                    if (!blankLine) {
                        age = if (line[0] == null) {
                            0
                        } else {
                            try {
                                line[0]!!.toInt()
                            } catch (nfe: NumberFormatException) {
                                0
                            }
                        }
                        sex = if (line[1] == null) {
                            ""
                        } else {
                            line[1]!!
                        }
                        probability75 = if (line[2] == null) {
                            0
                        } else {
                            try {
                                line[2]!!.toInt()
                            } catch (nfe: NumberFormatException) {
                                0
                            }
                        }
                        probability50 = if (line[3] == null) {
                            0
                        } else {
                            try {
                                line[3]!!.toInt()
                            } catch (nfe: NumberFormatException) {
                                0
                            }
                        }
                        probability25 = if (line[4] == null) {
                            0
                        } else {
                            try {
                                line[4]!!.toInt()
                            } catch (nfe: NumberFormatException) {
                                0
                            }
                        }

                        // If the input data is valid write the record to the database
                        if (age != 0 && sex != "") {
                            MainActivity.retirePlanDatabase?.expectanciesRecordsDao()?.
                            addExpectancyRecord(ExpectancyRecord(age, sex, probability75, probability50, probability25))!!
                        }
                    }

                    // Read the next input line
                    line = csvReader.readNext()
                }
            } else {
                results = context.getString(R.string.bad_header)
            }
            inputStream.close()
            csvReader.close()

        } catch (e: FileNotFoundException) {
            results = context.getString(R.string.file_not_found)
        } catch (e: IOException) {
            results = context.getString(R.string.file_read_error)
        }
        return results
    }

    // Import the tax table file
    fun importTaxTable(fileUri: Uri?, context: Context): String {

        var line: Array<String?>?
        var blankLine: Boolean
        var results = context.getString(R.string.import_complete)
        var rangeBottom: Double
        var rangeTop: Double
        var single: Double
        var marriedJointly: Double
        var marriedSeparate: Double
        var household: Double

        // Connect to the input file using an InputStreamReader and a CSVReader
        try {
            val inputStream: InputStream = context.contentResolver.openInputStream(fileUri!!)!!
            val inputStreamReader = InputStreamReader(inputStream)
            val csvReader = CSVReader(inputStreamReader)

            // Verify the validity of the header row
            if (validHeader(csvReader.readNext(), 6)) {

                // Clear out all existing life expectancy data
                MainActivity.retirePlanDatabase?.taxTableRecordsDao()?.deleteAllTaxTable()!!

                // Read the lines from the input CSV file
                line = csvReader.readNext()
                while (line != null) {

                    // Check for and skip any blank lines
                    blankLine = true
                    for (item in line) {
                        if (item!!.trim { it <= ' ' }.isNotEmpty()) {
                            blankLine = false
                        }
                    }

                    // Process all lines that are not blank
                    if (!blankLine) {
                        rangeBottom = if (line[0] == null) {
                            0.0
                        } else {
                            try {
                                line[0]!!.toDouble()
                            } catch (nfe: NumberFormatException) {
                                0.0
                            }
                        }
                        rangeTop = if (line[1] == null) {
                            0.0
                        } else {
                            try {
                                line[1]!!.toDouble()
                            } catch (nfe: NumberFormatException) {
                                0.0
                            }
                        }
                        single = if (line[2] == null) {
                            0.0
                        } else {
                            try {
                                line[2]!!.toDouble()
                            } catch (nfe: NumberFormatException) {
                                0.0
                            }
                        }
                        marriedJointly = if (line[3] == null) {
                            0.0
                        } else {
                            try {
                                line[3]!!.toDouble()
                            } catch (nfe: NumberFormatException) {
                                0.0
                            }
                        }
                        marriedSeparate = if (line[4] == null) {
                            0.0
                        } else {
                            try {
                                line[4]!!.toDouble()
                            } catch (nfe: NumberFormatException) {
                                0.0
                            }
                        }
                        household = if (line[5] == null) {
                            0.0
                        } else {
                            try {
                                line[5]!!.toDouble()
                            } catch (nfe: NumberFormatException) {
                                0.0
                            }
                        }

                        // If the input data is valid write the record to the database
                        if (rangeBottom > 0.1 && single > 0.1) {
                            MainActivity.retirePlanDatabase?.taxTableRecordsDao()?.
                                addTaxTableRecord(TaxTableRecord(rangeBottom, rangeTop, single, marriedJointly, marriedSeparate,
                                        household))!!
                        }
                    }

                    // Read the next input line
                    line = csvReader.readNext()
                }
            } else {
                results = context.getString(R.string.bad_header)
            }
            inputStream.close()
            csvReader.close()

        } catch (e: FileNotFoundException) {
            results = context.getString(R.string.file_not_found)
        } catch (e: IOException) {
            results = context.getString(R.string.file_read_error)
        }
        return results
    }
}