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
import androidx.recyclerview.widget.ItemTouchHelper
import com.radlsuttonedmn.retireplan.databinding.FragmentPortfolioBinding

/**
 * Fragment that enables entry of portfolio balances
 */

class PortfolioFragment : Fragment() {

    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var portfolioAdapter: PortfolioAdapter
    private lateinit var portfolioViewModel: PortfolioViewModel

    // Override the parent class onCreateView method
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentPortfolioBinding.inflate(layoutInflater, container, false)
        val rootView = binding.root

        // Get the name from the preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val name = preferences.getString("Individual", "")!!

        // Start a new portfolio list
        portfolioViewModel = ViewModelProviders.of(this).get(PortfolioViewModel::class.java)
        portfolioViewModel.getPortfolioRecords(name)

        // Add the text to the floating action button
        binding.buttonAddEntry.setImageBitmap(MainActivity.textAsBitmap(getString(R.string.add), requireContext()))

        // Add a list divider to the portfolio recyclerView
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.list_divider)!!)
        binding.recyclerViewPortfolio.addItemDecoration(divider)

        // Create an adapter and pass in the portfolio balances data
        portfolioAdapter = PortfolioAdapter(requireContext(), portfolioViewModel, rootView)

        // Attach the adapter to the recyclerView to populate the items
        binding.recyclerViewPortfolio.adapter = portfolioAdapter

        // Attach a swipe listener to enable swipe to delete a portfolio balance
        val callback = SimpleItemTouchHelperCallback(portfolioAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.recyclerViewPortfolio)

        // Set a layout manager to position the items
        binding.recyclerViewPortfolio.layoutManager = LinearLayoutManager(requireContext())

        // Set an on click listener on the add entry button
        binding.buttonAddEntry.setOnClickListener {
            val portfolioDialog = PortfolioDialog("add", rootView, portfolioViewModel, 0, object: DialogCallback {
                override fun dialogCallback() {

                    // Get the portfolio balances from the database and update the adapter
                    portfolioViewModel.getPortfolioRecords(name)
                    portfolioAdapter.notifyDataSetChanged()
                    MainActivity.showSnackBar(rootView, getString(R.string.update_complete))
                }
            })
            portfolioDialog.show(requireActivity().supportFragmentManager.beginTransaction(), "SOME_TAG")
        }

        return rootView
    }
}
