package com.radlsuttonedmn.retireplan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.radlsuttonedmn.retireplan.databinding.FragmentMainBinding


/**
 * Main fragment that displays the main financial data
 */

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var earningsViewModel: EarningsViewModel
    private lateinit var portfolioViewModel: PortfolioViewModel

    // Override the parent class onCreateView method
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        val rootView = binding.root

        // Get the name from the preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val name = preferences.getString("Individual", "")!!

        // Connect to the data model resources
        val mortgage = Mortgage(requireContext(), name)
        earningsViewModel = ViewModelProviders.of(this).get(EarningsViewModel::class.java)
        earningsViewModel.getEarnings(requireContext(), name)
        portfolioViewModel = ViewModelProviders.of(this).get(PortfolioViewModel::class.java)
        portfolioViewModel.getPortfolioRecords(name)

        // Display the mortgage payoff date
        binding.textViewMortgagePayoffDate.text = mortgage.calculatePayoffDate()

        // Display the mortgage monthly payoff amount
        binding.textViewMortgagePayoffAmount.text =
                String.format("%.2f", mortgage.calculatePayoffAmount())

        // Display the mortgage equity amount
        binding.textViewMortgageEquity.text =
                String.format("%.2f", mortgage.calculateEquity())

        // Display the monthly social security amount
        val monthlySSI = earningsViewModel.calculateMonthlySocialSecurity(context, name)
        binding.textViewMonthlySocialSecurity.text = String.format("%.2f", monthlySSI)

        // Display the portfolio end date
        binding.textViewPortfolioEndDate.text = portfolioViewModel.getPortfolioEndDate(context, name)

        // Display the monthly after tax income
        val monthlyIRA = portfolioViewModel.getMonthlyWithdrawal(requireContext(), name)
        val monthlyIRAValue: Double = if (monthlyIRA == "") {
            0.0
        } else {
            monthlyIRA.toDouble()
        }
        binding.textViewAfterTaxMonthly.text = String.format("%.2f", earningsViewModel.calculateAfterTaxMonthly(requireContext(),
                monthlyIRAValue, monthlySSI, name))

        return rootView
    }
}
