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
import com.radlsuttonedmn.retireplan.databinding.ExpectancyDialogBinding

class ExpectancyDialog(private val mode: String, private val rootView: View, private val expectanciesViewModel: ExpectanciesViewModel,
                       private val index: Int, private val callback: DialogCallback): DialogFragment() {

    // Override the parent class onCreateDialog method
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Create an alert dialog and inflate a custom layout
        val builder = AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle)
        val binding = ExpectancyDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        val sexesList = resources.getStringArray(R.array.sexes_list)
        var sex = ""
        var key: Long = 0

        // Initialize the sex spinner
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, sexesList)
        binding.spinnerSex.adapter = adapter

        // If an existing entry is being updated, fill the UI elements
        if (mode == "update") {
            if (expectanciesViewModel.getSex(index) == "") {
                binding.spinnerSex.setSelection(0)
            } else {
                binding.spinnerSex.setSelection(sexesList.indexOf(expectanciesViewModel.getSex(index)))
            }
            binding.editTextAge.setText(expectanciesViewModel.getAge(index).toString())
            binding.editTextProbability75.setText(expectanciesViewModel.getProbability75(index).toString())
            binding.editTextProbability50.setText(expectanciesViewModel.getProbability50(index).toString())
            binding.editTextProbability25.setText(expectanciesViewModel.getProbability25(index).toString())
            key = expectanciesViewModel.getKey(index)
        }

        builder.setMessage(getString(R.string.enter_life_expectancy))

        // Add a spinner selection listener to update the sex value
        binding.spinnerSex.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                sex = sexesList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        // Save the entered life expectancy when the positive button is clicked
        builder.setPositiveButton(getString(R.string.action_done)) { _, _ ->

            val age = binding.editTextAge.text.toString()
            var probability75 = binding.editTextProbability75.text.toString()
            var probability50 = binding.editTextProbability50.text.toString()
            var probability25 = binding.editTextProbability25.text.toString()

            // Validate the entered values
            if (age == "" || sex == "") {
                MainActivity.showSnackBar(rootView, getString(R.string.blank_values))
            } else {
                if (age.toInt() < 50) {
                    MainActivity.showSnackBar(rootView, getString(R.string.invalid_values))
                } else {
                    if (probability75 == "") {
                        probability75 = "0.0"
                    }
                    if (probability50 == "") {
                        probability50 = "0.0"
                    }
                    if (probability25 == "") {
                        probability25 = "0.0"
                    }
                    if (mode == "update") {
                        if (expectanciesViewModel.isDuplicateEdit(key, age.toInt(), sex)) {
                            MainActivity.showSnackBar(rootView, getString(R.string.duplicate_life_expectancy))
                        } else {
                            updateEntry(age, sex, probability75, probability50, probability25)
                        }
                    } else {
                        if (expectanciesViewModel.isDuplicate(age.toInt(), sex)) {
                            MainActivity.showSnackBar(rootView, getString(R.string.duplicate_life_expectancy))
                        } else {
                            addEntry(age, sex, probability75, probability50, probability25)
                        }
                    }
                }
            }
        }

        // Cancel the dialog if the negative button is pressed
        builder.setNegativeButton(getString(R.string.action_cancel)) { dialog, _ -> dialog.cancel()}
        return builder.create()
    }

    // Add the new life expectancy record to the expectancies list
    private fun addEntry(age: String, sex: String, probability75: String, probability50: String, probability25: String) {

        expectanciesViewModel.addExpectancy(age.toInt(), sex, probability75.toInt(), probability50.toInt(), probability25.toInt())
        callback.dialogCallback()
    }

    // Update an existing life expectancy record in the expectancies list
    private fun updateEntry(age: String, sex: String, probability75: String, probability50: String, probability25: String) {

        expectanciesViewModel.updateExpectancy(expectanciesViewModel.getKey(index), age.toInt(), sex, probability75.toInt(),
                probability50.toInt(), probability25.toInt())
        callback.dialogCallback()
    }
}
