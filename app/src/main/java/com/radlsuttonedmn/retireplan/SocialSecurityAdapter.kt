package com.radlsuttonedmn.retireplan

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.radlsuttonedmn.retireplan.databinding.EarningsItemBinding


class SocialSecurityAdapter(private val context: Context, private val earningsViewModel: EarningsViewModel, private val rootView: View) :
    RecyclerView.Adapter<SocialSecurityAdapter.EarningsViewHolder>() {

    // Method called by the item touch helper to delete an earnings record
    fun onItemDismiss(position: Int) {

        earningsViewModel.deleteEarnings(position)
        notifyItemRemoved(position)
    }

    // Override the parent class's getItemCount method
    override fun getItemCount(): Int {
        return earningsViewModel.getCount()
    }

    // Inflate the layout from XML and return the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarningsViewHolder {

        val binding =
            EarningsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EarningsViewHolder(binding)
    }

    // Populate data into the item through the holder
    override fun onBindViewHolder(holder: EarningsViewHolder, position: Int) {

        // Set item views based on the views and data model
        with (holder) {
            binding.textViewYear.text = earningsViewModel.getYear(holder.adapterPosition).toString()
            binding.textViewReportedEarnings.text =
                String.format("%.2f", earningsViewModel.getReportedEarnings(holder.adapterPosition))
            binding.textViewAverageEarnings.text =
                String.format("%.2f", earningsViewModel.getAverageEarnings(holder.adapterPosition))
            binding.textViewAdjustedEarnings.text =
                String.format("%.2f", earningsViewModel.getAdjustedEarnings(holder.adapterPosition))
        }

            // Set an on click listener on each earnings entry
        holder.itemView.setOnClickListener {

            val earningsDialog = EarningsDialog("update", rootView, earningsViewModel, holder.adapterPosition,
                object : DialogCallback {
                    override fun dialogCallback() {
                        // Get the name from the preferences
                        val preferences =
                            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                        val name = preferences.getString("Individual", "")!!

                        // Get the earnings records from the database and update the adapter
                        earningsViewModel.getEarnings(context, name)
                        notifyDataSetChanged()
                        MainActivity.showSnackBar(rootView, context.getString(R.string.update_complete))
                       }
                })
            earningsDialog.show(
                (context as AppCompatActivity).supportFragmentManager.beginTransaction(),
                    "SOME_TAG"
            )
        }
    }

    // Define the view holder as an inner class
    inner class EarningsViewHolder(val binding: EarningsItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
