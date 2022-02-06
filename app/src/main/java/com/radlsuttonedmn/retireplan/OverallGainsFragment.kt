package com.radlsuttonedmn.retireplan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.radlsuttonedmn.retireplan.databinding.FragmentOverallGainsBinding

/**
 * Fragment that display the overall gain results for the portfolio
 */

class OverallGainsFragment : Fragment() {

    private lateinit var binding: FragmentOverallGainsBinding
    private lateinit var overallGainsAdapter: OverallGainsAdapter
    private lateinit var portfolioViewModel: PortfolioViewModel

    // Override the parent class onCreateView method
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentOverallGainsBinding.inflate(layoutInflater, container, false)
        val rootView = binding.root

        // Get the name from the preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val name = preferences.getString("Individual", "")!!

        // Start a new portfolio list
        portfolioViewModel = ViewModelProviders.of(this).get(PortfolioViewModel::class.java)
        portfolioViewModel.getPortfolioRecords(name)
        val overallGainsList: ArrayList<OverallGainItem> = portfolioViewModel.getOverallGains()

        // Add a list divider to the overall gains recyclerView
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.list_divider)!!)
        binding.recyclerViewOverallGains.addItemDecoration(divider)

        // Create an adapter and pass in the overall gains data
        overallGainsAdapter = OverallGainsAdapter(overallGainsList)

        // Attach the adapter to the recyclerView to populate the items
        binding.recyclerViewOverallGains.adapter = overallGainsAdapter

        // Set a layout manager to position the items
        binding.recyclerViewOverallGains.layoutManager = LinearLayoutManager(requireContext())

        return rootView
    }
}
