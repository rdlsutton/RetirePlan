package com.radlsuttonedmn.retireplan

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.radlsuttonedmn.retireplan.databinding.ExpectancyItemBinding


class LifeExpectanciesAdapter(private val context: Context, private val expectanciesViewModel: ExpectanciesViewModel, private val rootView: View) :
        RecyclerView.Adapter<LifeExpectanciesAdapter.ExpectanciesViewHolder>() {

    // Method called by the item touch helper to delete an earnings record
    fun onItemDismiss(position: Int) {

        expectanciesViewModel.deleteExpectancy(position)
        notifyItemRemoved(position)
    }

    // Override the parent class's getItemCount method
    override fun getItemCount(): Int {
        return expectanciesViewModel.getCount()
    }

    // Inflate the layout from XML and return the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpectanciesViewHolder {

        val binding = ExpectancyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpectanciesViewHolder(binding)
    }

    // Populate data into the item through the holder
    override fun onBindViewHolder(holder: ExpectanciesViewHolder, position: Int) {

        // Set item views based on the views and data model
        with(holder) {
            binding.textViewSex.text = expectanciesViewModel.getSex(holder.adapterPosition)
            binding.textViewAge.text = expectanciesViewModel.getAge(holder.adapterPosition).toString()
            binding.textViewProbability75.text = expectanciesViewModel.getProbability75(holder.adapterPosition).toString()
            binding.textViewProbability50.text = expectanciesViewModel.getProbability50(holder.adapterPosition).toString()
            binding.textViewProbability25.text = expectanciesViewModel.getProbability25(holder.adapterPosition).toString()
        }

        // Set an on click listener on each expectancy entry
        holder.itemView.setOnClickListener {

            val expectancyDialog = ExpectancyDialog("update", rootView, expectanciesViewModel, holder.adapterPosition,
                    object: DialogCallback {
                        override fun dialogCallback() {

                            // Get the earnings records from the database and update the adapter
                            expectanciesViewModel.getExpectancies()
                            notifyDataSetChanged()
                            MainActivity.showSnackBar(rootView, context.getString(R.string.update_complete))
                        }
                    })
            expectancyDialog.show((context as AppCompatActivity).supportFragmentManager.beginTransaction(), "SOME_TAG")
        }
    }

    // Define the view holder as an inner class
    inner class ExpectanciesViewHolder(val binding: ExpectancyItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
