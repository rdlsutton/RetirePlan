package com.radlsuttonedmn.retireplan

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.radlsuttonedmn.retireplan.databinding.FragmentSnapshotBinding


/**
 * Fragment that enables the entry of various values used in the retirement calculations
 */

class SnapshotFragment : Fragment() {

    private lateinit var binding: FragmentSnapshotBinding
    private lateinit var earningsViewModel: EarningsViewModel
    private lateinit var portfolioViewModel: PortfolioViewModel

    // Override the parent class onCreateView method
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentSnapshotBinding.inflate(layoutInflater, container, false)
        val rootView = binding.root

        // Add the text to the floating action button
        binding.buttonSnapshot.setImageBitmap(MainActivity.textAsBitmap(getString(R.string.save), requireContext()))

        // Get the name from the preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val name = preferences.getString("Individual", "")!!

        // Connect to the data model resources
        val mortgage = Mortgage(requireContext(), name)
        portfolioViewModel = ViewModelProviders.of(this).get(PortfolioViewModel::class.java)
        portfolioViewModel.getPortfolioRecords(name)
        earningsViewModel = ViewModelProviders.of(this).get(EarningsViewModel::class.java)
        earningsViewModel.getEarnings(requireContext(), name)

        // Fill the user interface text view elements
        binding.textViewSnapMortgagePayoff.text = preferences.getString("SnapMortgagePayoff$name", "")!!
        binding.textViewSnapMortgageMonthly.text = preferences.getString("SnapMortgageMonthly$name", "")!!
        binding.textViewSnapMortgageEquity.text = preferences.getString("SnapMortgageEquity$name", "")!!
        binding.textViewSnapSocialSecurity.text = preferences.getString("SnapSocialSecurity$name", "")!!
        binding.textViewSnapPortfolioEnd.text = preferences.getString("SnapPortfolioDate$name", "")!!
        binding.textViewSnapAfterTaxMonthly.text = preferences.getString("SnapAfterTax$name", "")!!
        binding.textViewSnapMortgageBalance.text = preferences.getString("SnapMortgageBalance$name", "")!!
        binding.textViewSnapHouseValue.text = preferences.getString("SnapHouseValue$name", "")!!
        binding.textViewSnapMonthlyWithdrawal.text = preferences.getString("SnapMonthlyWithdrawal$name", "")!!
        binding.textViewSnapSSIStartYear.text = preferences.getString("SnapSSIStartYear$name", "")!!
        binding.textViewSnapSSIStartMonth.text = preferences.getString("SnapSSIStartMonth$name", "")!!

        // Add a button onClick listener
        binding.buttonSnapshot.setOnClickListener {

            // Save the entered values in the shared preferences
            saveValues(
                    name,
                    mortgage,
                    preferences,
                    binding,
                    rootView
            )
        }

        return rootView
    }

    private fun saveValues(
            name: String,
            mortgage: Mortgage,
            preferences: SharedPreferences,
            binding: FragmentSnapshotBinding,
            rootView: View
    ) {
        val editor = preferences.edit()

        // Save the mortgage payoff snapshot value
        val mortgagePayoff = mortgage.calculatePayoffDate()
        editor.putString("SnapMortgagePayoff$name", mortgagePayoff)
        editor.apply()
        binding.textViewSnapMortgagePayoff.text = mortgagePayoff

        // Save the mortgage monthly payoff snapshot value
        val mortgagePayoffAmount = String.format("%.2f", mortgage.calculatePayoffAmount())
        editor.putString("SnapMortgageMonthly$name", mortgagePayoffAmount)
        editor.apply()
        binding.textViewSnapMortgageMonthly.text = mortgagePayoffAmount

        // Save the mortgage equity snapshot value
        val mortgageEquity = mortgage.calculateEquity().toString()
        editor.putString("SnapMortgageEquity$name", mortgageEquity)
        editor.apply()
        binding.textViewSnapMortgageEquity.text = mortgageEquity

        // Save the social security snapshot value
        val socialSecurity = earningsViewModel.calculateMonthlySocialSecurity(context, name)
        editor.putString("SnapSocialSecurity$name", String.format("%.2f", socialSecurity))
        editor.apply()
        binding.textViewSnapSocialSecurity.text = String.format("%.2f", socialSecurity)

        // Save the portfolio end date snapshot value
        val portfolioDate = portfolioViewModel.getPortfolioEndDate(context, name)
        editor.putString("SnapPortfolioDate$name", portfolioDate)
        editor.apply()
        binding.textViewSnapPortfolioEnd.text = portfolioDate

        // Save the portfolio end date snapshot value
        val monthlyIRA = portfolioViewModel.getMonthlyWithdrawal(requireContext(), name)
        val monthlyIRAValue: Double = if (monthlyIRA == "") {
            0.0
        } else {
            monthlyIRA.toDouble()
        }
        val afterTaxMonthly = String.format("%.2f", earningsViewModel.calculateAfterTaxMonthly(requireContext(),
                monthlyIRAValue, socialSecurity, name))
        editor.putString("SnapAfterTax$name", afterTaxMonthly)
        editor.apply()
        binding.textViewSnapAfterTaxMonthly.text = afterTaxMonthly

        // Save the mortgage balance snapshot value
        editor.putString("SnapMortgageBalance$name", mortgage.balance.toString())
        editor.apply()
        binding.textViewSnapMortgageBalance.text = mortgage.balance.toString()

        // Save the house value snapshot value
        editor.putString("SnapHouseValue$name", String.format("%.2f", mortgage.houseValue))
        editor.apply()
        binding.textViewSnapHouseValue.text = String.format("%.2f", mortgage.houseValue)

        // Save the house value snapshot value
        val monthlyWithdrawal = portfolioViewModel.getMonthlyWithdrawal(
                requireContext(), name)
        editor.putString("SnapMonthlyWithdrawal$name", monthlyWithdrawal)
        editor.apply()
        binding.textViewSnapMonthlyWithdrawal.text = monthlyWithdrawal

        // Save the SSI Start Year snapshot value
        val sSIStartYear = earningsViewModel.getStartYear(requireContext(), name)
        editor.putString("SnapSSIStartYear$name", sSIStartYear)
        editor.apply()
        binding.textViewSnapSSIStartYear.text = sSIStartYear

        // Save the SSI Start Month snapshot value
        val sSIStartMonth = earningsViewModel.getStartMonth(requireContext(), name)
        editor.putString("SnapSSIStartMonth$name", sSIStartMonth)
        editor.apply()
        binding.textViewSnapSSIStartMonth.text = sSIStartMonth

        MainActivity.showSnackBar(rootView, getString(R.string.settings_saved))
    }
}
