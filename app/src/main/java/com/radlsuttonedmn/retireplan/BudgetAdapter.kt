package com.radlsuttonedmn.retireplan

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.radlsuttonedmn.retireplan.databinding.BudgetItemBinding


class BudgetAdapter(private val context: Context, private val budgetViewModel: BudgetViewModel, private val rootView: View) :
        RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    // Method called by the item touch helper to delete a budget item
    fun onItemDismiss(position: Int) {

        budgetViewModel.deleteBudgetItem(position)
        notifyItemRemoved(position)
    }

    // Override the parent class's getItemCount method
    override fun getItemCount(): Int {
        return budgetViewModel.getCount()
    }

    // Inflate the layout from XML and return the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {

        val binding = BudgetItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BudgetViewHolder(binding)
    }

    // Populate data into the item through the holder
    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {

        // Set item views based on the views and data model
        with(holder) {
            binding.textViewCategory.text = budgetViewModel.getCategory(holder.adapterPosition)
            binding.textViewAmount.text =
                String.format("%.2f", budgetViewModel.getAmount(holder.adapterPosition))
        }

        // Set an on click listener on each budget item
        holder.itemView.setOnClickListener {

            val budgetDialog = BudgetDialog("update", rootView, budgetViewModel, holder.adapterPosition,
                    object: DialogCallback {
                        override fun dialogCallback() {

                            // Get the name from the preferences
                            val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                            val name = preferences.getString("Individual", "")!!

                            // Get the budget item records from the database and update the adapter
                            budgetViewModel.getBudgetItems(name)
                            notifyDataSetChanged()
                            MainActivity.showSnackBar(rootView, context.getString(R.string.update_complete))
                        }
                    })
            budgetDialog.show((context as AppCompatActivity).supportFragmentManager.beginTransaction(), "SOME_TAG")
        }
    }

    // Define the view holder as an inner class
    inner class BudgetViewHolder(val binding: BudgetItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
