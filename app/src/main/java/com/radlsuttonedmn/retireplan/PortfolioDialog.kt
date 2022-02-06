package com.radlsuttonedmn.retireplan

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.radlsuttonedmn.retireplan.databinding.PortfolioDialogBinding


class PortfolioDialog(private val mode: String, private val rootView: View,
                      private val portfolioViewModel: PortfolioViewModel, private val index: Int,
                      private val callback: DialogCallback): DialogFragment() {

    // Override the parent class onCreateDialog method
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Create an alert dialog and inflate a custom layout
        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle)
        val binding = PortfolioDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        val monthList = resources.getStringArray(R.array.month_list)
        var month = ""
        var key: Long = 0

        // Initialize the month spinner
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, monthList)
        binding.spinnerMonth.adapter = adapter

        // If an existing entry is being updated, fill the UI elements
        if (mode == "update") {

            if (portfolioViewModel.getMonth(index) == "") {
                binding.spinnerMonth.setSelection(0)
            } else {
                binding.spinnerMonth.setSelection(monthList.indexOf(portfolioViewModel.getMonth(index)))
            }
            key = portfolioViewModel.getKey(index)

            // Initialize the year and balance edit texts
            binding.editTextYear.setText(portfolioViewModel.getYear(index).toString())
            binding.editTextBalance.setText(String.format("%.2f", portfolioViewModel.getBalance(index)))
        }

        builder.setMessage(getString(R.string.enter_balance))

        // Add a spinner selection listener to update the month value
        binding.spinnerMonth.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                month = monthList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        // Save the entered portfolio balance when the positive button is clicked
        builder.setPositiveButton(getString(R.string.action_done)) { _, _ ->

            val year = binding.editTextYear.text.toString()
            val balance = binding.editTextBalance.text.toString()

            // Get the name from the preferences
            val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            val name = preferences.getString("Individual", "")!!

            // Validate the entered values
            if (month == "" || year == "") {
                MainActivity.showSnackBar(rootView, getString(R.string.blank_values))
            } else {
                if (year.toInt() < 1900) {
                    MainActivity.showSnackBar(rootView, getString(R.string.invalid_values))
                } else {
                    if (mode == "update") {
                        if (portfolioViewModel.isDuplicateEdit(key, name, month, year.toInt())) {
                            MainActivity.showSnackBar(rootView, getString(R.string.duplicate_month_year))
                        } else {
                            updateRecord(name, month, year, balance)
                        }
                    } else {
                        if (portfolioViewModel.isDuplicate(name, month, year.toInt())) {
                            MainActivity.showSnackBar(rootView, getString(R.string.duplicate_month_year))
                        } else {
                            addRecord(name, month, year, balance)
                        }
                    }
                }
            }
        }

        // Cancel the dialog if the negative button is pressed
        builder.setNegativeButton(getString(R.string.action_cancel)) { dialog, _ -> dialog.cancel()}
        return builder.create()
    }

    // Add the new portfolio balance record to the portfolio list
    private fun addRecord(name: String, month: String, year: String, balance: String) {

        portfolioViewModel.addRecord(name, month, year.toInt(), balance.toDouble())
        callback.dialogCallback()
    }

    // Update an existing portfolio balance record in the portfolio list
    private fun updateRecord(name: String, month: String, year: String, balance: String) {

        portfolioViewModel.updateRecord(portfolioViewModel.getKey(index), name, month, year.toInt(), balance.toDouble())
        callback.dialogCallback()
    }
}
