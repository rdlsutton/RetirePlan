package com.radlsuttonedmn.retireplan

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.radlsuttonedmn.retireplan.databinding.FragmentSettingsBinding


/**
 * Fragment that enables the entry of various values used in the retirement calculations
 */

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var earningsViewModel: EarningsViewModel
    private lateinit var portfolioViewModel: PortfolioViewModel

    // Override the parent class onCreateView method
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        val rootView = binding.root

        // Add the text to the floating action button
        binding.buttonSaveValues.setImageBitmap(MainActivity.textAsBitmap(getString(R.string.save), requireContext()))

        // Get the name from the preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        var name = preferences.getString("Individual", "")!!

        // Connect to the data model resources
        var mortgage = Mortgage(requireContext(), name)
        portfolioViewModel = ViewModelProviders.of(this).get(PortfolioViewModel::class.java)
        earningsViewModel = ViewModelProviders.of(this).get(EarningsViewModel::class.java)

        // Get the list of individual's names from the database
        val individualValues: List<Individual> =
                MainActivity.retirePlanDatabase?.individualsDao()?.getNames()!!
        val individuals = individualValues.toArrayList()
        val names = ArrayList<String>()
        for (individual in individuals) {
            names.add(individual.name)
        }

        // Add the list of names as suggestions to the auto complete text view
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.suggestions_text,
            names
        )
        binding.textViewName.setAdapter(adapter)

        // Set the threshold for the AutoCompleteTextView
        binding.textViewName.threshold = 1

        // Fill the user interface elements with the data values
        binding.textViewName.setText(name)
        binding.editTextMortgageBalance.setText(String.format("%.2f", mortgage.balance))
        binding.editTextInterestRate.setText(mortgage.interestRate.toString())
        binding.editTextPrincipalInterest.setText(String.format("%.2f", mortgage.principalInterest))
        binding.editTextHouseValue.setText(String.format("%.2f", mortgage.houseValue))
        binding.editTextGrowthRate.setText(portfolioViewModel.getGrowthRate(requireContext(), name))
        binding.editTextMonthlyWithdrawal.setText(
            portfolioViewModel.getMonthlyWithdrawal(
                requireContext(),
                name
            )
        )
        binding.editTextAge60Year.setText(earningsViewModel.getAge60Year(requireContext(), name))
        binding.editTextStartYear.setText(earningsViewModel.getStartYear(requireContext(), name))

        // Initialize the filing status spinner
        val statusList = resources.getStringArray(R.array.status_list)
        var filingStatus = earningsViewModel.getFilingStatus(requireContext(), name)
        val statusAdapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, statusList
        )
        binding.spinnerFilingStatus.adapter = statusAdapter
        if (filingStatus == "") {
            binding.spinnerFilingStatus.setSelection(0)
        } else {
            binding.spinnerFilingStatus.setSelection(statusList.indexOf(filingStatus))
        }

        // Add a spinner selection listener to update the filing status value
        binding.spinnerFilingStatus.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                filingStatus = statusList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        // Initialize the start month spinner
        val monthList = resources.getStringArray(R.array.month_list)
        var startMonth = earningsViewModel.getStartMonth(requireContext(), name)
        val monthAdapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, monthList
        )
        binding.spinnerStartMonth.adapter = monthAdapter
        if (startMonth == "") {
            binding.spinnerStartMonth.setSelection(0)
        } else {
            binding.spinnerStartMonth.setSelection(monthList.indexOf(startMonth))
        }

        // Add a spinner selection listener to update the start month value
        binding.spinnerStartMonth.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                startMonth = monthList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        // Add a listener to the name text view in order to change the settings to the selected individual
        binding.textViewName.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            // Update the name to the new setting and switch other settings to the new individual
            override fun afterTextChanged(s: Editable) {
                name = binding.textViewName.text.toString()
                mortgage = Mortgage(requireContext(), name)
                binding.editTextMortgageBalance.setText(String.format("%.2f", mortgage.balance))
                binding.editTextInterestRate.setText(mortgage.interestRate.toString())
                binding.editTextPrincipalInterest.setText(String.format("%.2f", mortgage.principalInterest))
                binding.editTextHouseValue.setText(String.format("%.2f", mortgage.houseValue))
                binding.editTextGrowthRate.setText(portfolioViewModel.getGrowthRate(requireContext(), name))
                binding.editTextMonthlyWithdrawal.setText(
                    portfolioViewModel.getMonthlyWithdrawal(
                        requireContext(),
                        name
                    )
                )
                binding.editTextAge60Year.setText(earningsViewModel.getAge60Year(requireContext(), name))
                binding.editTextStartYear.setText(earningsViewModel.getStartYear(requireContext(), name))
            }
        })

        // Add a button onClick listener
        binding.buttonSaveValues.setOnClickListener {

            // Save the entered values in the shared preferences
            saveValues(
                binding.textViewName.text.toString(),
                mortgage,
                binding.editTextMortgageBalance.text.toString(),
                binding.editTextInterestRate.text.toString(),
                binding.editTextPrincipalInterest.text.toString(),
                binding.editTextHouseValue.text.toString(),
                binding.editTextGrowthRate.text.toString(),
                binding.editTextMonthlyWithdrawal.text.toString(),
                filingStatus,
                binding.editTextAge60Year.text.toString(),
                startMonth,
                binding.editTextStartYear.text.toString(),
                rootView
            )
        }

        return rootView
    }

    private fun saveValues(
        name: String,
        mortgage: Mortgage,
        balance: String,
        interestRate: String,
        principalInterest: String,
        houseValue: String,
        growthRate: String,
        monthlyWithdrawal: String,
        filingStatus: String,
        age60Year: String,
        startMonth: String,
        startYear: String,
        rootView: View
    ) {

        // Verify that a name value exists
        if (name == "") {
            MainActivity.showSnackBar(rootView, getString(R.string.blank_name))
            return
        }

        // Save the name value to the shared preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("Individual", name)
        editor.apply()

        // Save the name to the database if it is a new name
        if (!MainActivity.retirePlanDatabase?.individualsDao()?.
                nameExists(name)!!) {
            MainActivity.retirePlanDatabase?.individualsDao()?.addName(Individual(name))
        }

        // Save the edit text balance value in the mortgage instance
        if (balance.trim() == "") {
            mortgage.balance = 0.0
        } else {
            mortgage.balance = balance.toDouble()
        }

        // Save the edit text interest rate value in the mortgage instance
        if (interestRate.trim() == "") {
            mortgage.interestRate = 0.0
        } else {
            mortgage.interestRate = interestRate.toDouble()
        }

        // Save the edit text principal interest value in the mortgage instance
        if (principalInterest.trim() == "") {
            mortgage.principalInterest = 0.0
        } else {
            mortgage.principalInterest = principalInterest.toDouble()
        }

        // Save the edit text house value amount in the mortgage instance
        if (houseValue.trim() == "") {
            mortgage.houseValue = 0.0
        } else {
            mortgage.houseValue = houseValue.toDouble()
        }

        // Save the mortgage settings data
        mortgage.saveMortgage(requireContext(), name)

        // Save the portfolio settings
        portfolioViewModel.saveGrowthRate(requireContext(), growthRate, name)
        portfolioViewModel.saveMonthlyWithdrawal(requireContext(), monthlyWithdrawal, name)

        // Save the filing  status and social security start date data
        earningsViewModel.saveFilingStatus(requireContext(), filingStatus, name)
        earningsViewModel.saveAge60Year(requireContext(), age60Year, name)
        earningsViewModel.saveStartDate(requireContext(), startMonth, startYear, name)

        MainActivity.showSnackBar(rootView, getString(R.string.settings_saved))
    }

    // Utility function to convert a list to an array list
    private fun <T> List<T>.toArrayList(): ArrayList<T>{
        return ArrayList(this)
    }
}
