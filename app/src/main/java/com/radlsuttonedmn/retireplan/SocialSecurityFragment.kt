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
import com.radlsuttonedmn.retireplan.databinding.FragmentSocialSecurityBinding


/**
 * Fragment that enables entry of social security earnings
 */

class SocialSecurityFragment : Fragment() {

    private lateinit var binding: FragmentSocialSecurityBinding
    private lateinit var socialSecurityAdapter: SocialSecurityAdapter
    private lateinit var earningsViewModel: EarningsViewModel

    // Override the parent class onCreateView method
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentSocialSecurityBinding.inflate(layoutInflater, container, false)
        val rootView = binding.root

        // Get the name from the preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val name = preferences.getString("Individual", "")!!

        // Start a new earnings list
        earningsViewModel = ViewModelProviders.of(this).get(EarningsViewModel::class.java)
        earningsViewModel.getEarnings(requireContext(), name)

        // Add the text to the floating action button
        binding.buttonAddEarningsEntry.setImageBitmap(MainActivity.textAsBitmap(getString(R.string.add), requireContext()))

        // Add a list divider to the earnings record recyclerView
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.list_divider)!!)
        binding.recyclerViewEarnings.addItemDecoration(divider)

        // Create an adapter and pass in the earnings list data
        socialSecurityAdapter = SocialSecurityAdapter(requireContext(), earningsViewModel, rootView)

        // Attach the adapter to the recyclerView to populate the items
        binding.recyclerViewEarnings.adapter = socialSecurityAdapter

        // Attach a swipe listener to enable swipe to delete an earnings record
        val callback = SimpleItemTouchHelperCallback(socialSecurityAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.recyclerViewEarnings)

        // Set a layout manager to position the items
        binding.recyclerViewEarnings.layoutManager = LinearLayoutManager(requireContext())

        // Set an on click listener on the add entry button
        binding.buttonAddEarningsEntry.setOnClickListener {
            val earningsDialog = EarningsDialog("add", rootView, earningsViewModel, 0, object : DialogCallback {
                override fun dialogCallback() {

                    // Get the earnings records from the database and update the adapter
                    earningsViewModel.getEarnings(requireContext(), name)
                    socialSecurityAdapter.notifyDataSetChanged()
                    MainActivity.showSnackBar(rootView, getString(R.string.update_complete))
                }
            })
            earningsDialog.show(requireActivity().supportFragmentManager.beginTransaction(), "SOME_TAG")
        }

        return rootView
    }
}
