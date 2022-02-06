package com.radlsuttonedmn.retireplan

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.radlsuttonedmn.retireplan.databinding.EarningsDialogBinding

class EarningsDialog(private val mode: String, private val rootView: View, private val earningsViewModel: EarningsViewModel,
                     private val index: Int, private val callback: DialogCallback): DialogFragment() {

        // Override the parent class onCreateDialog method
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Create an alert dialog and inflate a custom layout
        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle)
        val binding = EarningsDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        var key: Long = 0

        // Get the name from the preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val name = preferences.getString("Individual", "")!!

        // If an existing entry is being updated, fill the UI elements
        if (mode == "update") {
            binding.editTextYear.setText(earningsViewModel.getYear(index).toString())
            binding.editTextReportedEarnings.setText(
                String.format("%.2f",earningsViewModel.getReportedEarnings(index)))
            binding.editTextAverageEarnings.setText(
                String.format("%.2f",earningsViewModel.getAverageEarnings(index)))
            key = earningsViewModel.getKey(index)
        }

        builder.setMessage(getString(R.string.enter_earnings))

        // Save the entered earnings when the positive button is clicked
        builder.setPositiveButton(getString(R.string.action_done)) { _, _ ->

            val year = binding.editTextYear.text.toString()
            val reportedEarnings = binding.editTextReportedEarnings.text.toString()
            var averageEarnings = binding.editTextAverageEarnings.text.toString()

            // Validate the entered values
            if (year == "" || reportedEarnings == "") {
                MainActivity.showSnackBar(rootView, getString(R.string.blank_values))
            } else {
                if (year.toInt() < 1900 || reportedEarnings.toDouble() < 1.0) {
                    MainActivity.showSnackBar(rootView, getString(R.string.invalid_values))
                } else {
                    if (averageEarnings == "") {
                        averageEarnings = "0.0"
                    }
                    if (mode == "update") {
                        if (earningsViewModel.isDuplicateEdit(key, name, year.toInt())) {
                            MainActivity.showSnackBar(rootView, getString(R.string.duplicate_year))
                        } else {
                            updateEntry(name, year, reportedEarnings, averageEarnings)
                        }
                    } else {
                        if (earningsViewModel.isDuplicate(name, year.toInt())) {
                            MainActivity.showSnackBar(rootView, getString(R.string.duplicate_year))
                        } else {
                            addEntry(name, year, reportedEarnings, averageEarnings)
                        }
                    }
                }
            }
        }

        // Cancel the dialog if the negative button is pressed
        builder.setNegativeButton(getString(R.string.action_cancel)) { dialog, _ -> dialog.cancel()}
        return builder.create()
    }

    // Add the new earnings record to the earnings list
    private fun addEntry(name: String, year: String, reportedEarnings: String, averageEarnings: String) {

        earningsViewModel.addEarnings(name, year.toInt(), reportedEarnings.toDouble(), averageEarnings.toDouble())
        callback.dialogCallback()
    }

    // Update an existing earnings record in the earnings list
    private fun updateEntry(name: String, year: String, reportedEarnings: String, averageEarnings: String) {

        earningsViewModel.updateEarnings(earningsViewModel.getKey(index), name, year.toInt(), reportedEarnings.toDouble(),
            averageEarnings.toDouble())
        callback.dialogCallback()
    }
}
