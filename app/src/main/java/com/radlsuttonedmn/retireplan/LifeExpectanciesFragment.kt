package com.radlsuttonedmn.retireplan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.radlsuttonedmn.retireplan.databinding.FragmentLifeExpectanciesBinding


/**
 * Fragment that enables entry of life expectancy values
 */

class LifeExpectanciesFragment : Fragment() {

    private lateinit var binding: FragmentLifeExpectanciesBinding
    private lateinit var lifeExpectanciesAdapter: LifeExpectanciesAdapter
    private lateinit var expectanciesViewModel: ExpectanciesViewModel

    // Override the parent class onCreateView method
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentLifeExpectanciesBinding.inflate(layoutInflater, container, false)
        val rootView = binding.root

        // Start a new expectancies list
        expectanciesViewModel = ViewModelProviders.of(this).get(ExpectanciesViewModel::class.java)
        expectanciesViewModel.getExpectancies()

        // Add the text to the floating action button
        binding.buttonAddExpectancyEntry.setImageBitmap(MainActivity.textAsBitmap(getString(R.string.add), requireContext()))

        // Add a list divider to the life expectancies recyclerView
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.list_divider)!!)
        binding.recyclerViewExpectancies.addItemDecoration(divider)

        // Create an adapter and pass in the life expectancies list data
        lifeExpectanciesAdapter = LifeExpectanciesAdapter(requireContext(), expectanciesViewModel, rootView)

        // Attach the adapter to the recyclerView to populate the items
        binding.recyclerViewExpectancies.adapter = lifeExpectanciesAdapter

        // Attach a swipe listener to enable swipe to delete a life expectancy record
        val callback = SimpleItemTouchHelperCallback(lifeExpectanciesAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.recyclerViewExpectancies)

        // Set a layout manager to position the items
        binding.recyclerViewExpectancies.layoutManager = LinearLayoutManager(requireContext())

        // Set an on click listener on the add entry button
        binding.buttonAddExpectancyEntry.setOnClickListener {
            val expectancyDialog = ExpectancyDialog("add", rootView, expectanciesViewModel, 0, object : DialogCallback {
                override fun dialogCallback() {

                    // Get the life expectancy records from the database and update the adapter
                    expectanciesViewModel.getExpectancies()
                    lifeExpectanciesAdapter.notifyDataSetChanged()
                    MainActivity.showSnackBar(rootView, getString(R.string.update_complete))
                }
            })
            expectancyDialog.show(requireActivity().supportFragmentManager.beginTransaction(), "SOME_TAG")
        }

        return rootView
    }
}
