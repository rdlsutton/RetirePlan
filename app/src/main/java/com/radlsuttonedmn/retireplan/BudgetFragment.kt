package com.radlsuttonedmn.retireplan

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.radlsuttonedmn.retireplan.databinding.FragmentBudgetBinding

/**
 * Fragment that allows the display of a monthly budget
 */

class BudgetFragment : Fragment() {

    private lateinit var binding: FragmentBudgetBinding
    private lateinit var budgetAdapter: BudgetAdapter
    private lateinit var budgetViewModel: BudgetViewModel

    // Override the parent class onCreateView method
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentBudgetBinding.inflate(layoutInflater, container, false)
        val rootView = binding.root

        // Get the name from the preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val name = preferences.getString("Individual", "")!!

        // Start a new budget list
        budgetViewModel = ViewModelProviders.of(this).get(BudgetViewModel::class.java)
        budgetViewModel.getBudgetItems(name)

        // Add the text to the floating action button
        binding.buttonAddBudgetItem.setImageBitmap(MainActivity.textAsBitmap(getString(R.string.add), requireContext()))

        // Add a list divider to the budget recyclerView
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.list_divider)!!)
        binding.recyclerViewBudget.addItemDecoration(divider)

        // Create an adapter and pass in the budget data
        budgetAdapter = BudgetAdapter(requireContext(), budgetViewModel, rootView)

        // Attach the adapter to the recyclerView to populate the items
        binding.recyclerViewBudget.adapter = budgetAdapter

        // Attach a swipe listener to enable swipe to delete a budget item
        val callback = SimpleItemTouchHelperCallback(budgetAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.recyclerViewBudget)

        // Set a layout manager to position the items
        binding.recyclerViewBudget.layoutManager = LinearLayoutManager(requireContext())

        // Set an on click listener on the add budget item button
        binding.buttonAddBudgetItem.setOnClickListener {
            val budgetDialog = BudgetDialog("add", rootView, budgetViewModel, 0, object: DialogCallback {
                override fun dialogCallback() {

                    // Get the budget items from the database and update the adapter
                    budgetViewModel.getBudgetItems(name)
                    budgetAdapter.notifyDataSetChanged()
                    MainActivity.showSnackBar(rootView, getString(R.string.update_complete))
                }
            })
            budgetDialog.show(requireActivity().supportFragmentManager.beginTransaction(), "SOME_TAG")
        }

        return rootView
    }
}
