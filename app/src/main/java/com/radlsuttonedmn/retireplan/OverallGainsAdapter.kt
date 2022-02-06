package com.radlsuttonedmn.retireplan

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import com.radlsuttonedmn.retireplan.databinding.OverallGainItemBinding


class OverallGainsAdapter(private val overallGainsList: ArrayList<OverallGainItem>) :
        RecyclerView.Adapter<OverallGainsAdapter.OverallGainsViewHolder>() {

    // Override the parent class's getItemCount method
    override fun getItemCount(): Int {
        return overallGainsList.size
    }

    // Inflate the layout from XML and return the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverallGainsViewHolder {

        val binding = OverallGainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OverallGainsViewHolder(binding)
    }

    // Populate data into the item through the holder
    override fun onBindViewHolder(holder: OverallGainsViewHolder, position: Int) {

        // Set item views based on the views and data model
        with(holder) {
            binding.textViewGainMonth.text = overallGainsList[holder.adapterPosition].month
            binding.textViewGainYear.text = overallGainsList[holder.adapterPosition].year.toString()
            binding.textViewOverallGain.text =
                String.format("%.2f", overallGainsList[holder.adapterPosition].overallGain)
        }
    }

    // Define the view holder as an inner class
    inner class OverallGainsViewHolder(val binding: OverallGainItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
