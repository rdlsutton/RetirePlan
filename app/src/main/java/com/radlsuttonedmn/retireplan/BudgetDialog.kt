package com.radlsuttonedmn.retireplan

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.radlsuttonedmn.retireplan.databinding.BudgetDialogBinding


class BudgetDialog(private val mode: String, private val rootView: View,
                   private val budgetViewModel: BudgetViewModel, private val index: Int,
                   private val callback: DialogCallback): DialogFragment() {

    // Override the parent class onCreateDialog method
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Create an alert dialog and inflate a custom layout
        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle)
        val binding = BudgetDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        var key: Long = 0

        // If an existing entry is being updated, fill the UI elements
        if (mode == "update") {

            key = budgetViewModel.getKey(index)

            // Initialize the year and balance edit texts
            binding.editTextDialogCategory.setText(budgetViewModel.getCategory(index))
            binding.editTextDialogAmount.setText(String.format("%.2f", budgetViewModel.getAmount(index)))
        }

        builder.setMessage(getString(R.string.enter_budget_item))

        // Save the entered portfolio balance when the positive button is clicked
        builder.setPositiveButton(getString(R.string.action_done)) { _, _ ->

            val category = binding.editTextDialogCategory.text.toString()
            val amount = binding.editTextDialogAmount.text.toString()

            // Get the name from the preferences
            val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            val name = preferences.getString("Individual", "")!!

            // Validate the entered values
            if (category == "") {
                MainActivity.showSnackBar(rootView, getString(R.string.blank_values))
            } else {
                if (mode == "update") {
                    if (budgetViewModel.isDuplicateEdit(key, name, category)) {
                        MainActivity.showSnackBar(rootView, getString(R.string.duplicate_category))
                    } else {
                        updateBudgetItem(name, category, amount)
                    }
                } else {
                    if (budgetViewModel.isDuplicate(name, category)) {
                        MainActivity.showSnackBar(rootView, getString(R.string.duplicate_category))
                    } else {
                        addBudgetItem(name, category, amount)
                    }
                }
            }
        }

        // Cancel the dialog if the negative button is pressed
        builder.setNegativeButton(getString(R.string.action_cancel)) { dialog, _ -> dialog.cancel()}
        return builder.create()
    }

    // Add the new budget item to the budget list
    private fun addBudgetItem(name: String, category: String, amount: String) {

        budgetViewModel.addBudgetItem(name, category, amount.toDouble())
        callback.dialogCallback()
    }

    // Update an existing budget item in the budget list
    private fun updateBudgetItem(name: String, category: String, amount: String) {

        budgetViewModel.updateBudgetItem(budgetViewModel.getKey(index), name, category, amount.toDouble())
        callback.dialogCallback()
    }
}
