package com.radlsuttonedmn.retireplan

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.radlsuttonedmn.retireplan.databinding.PortfolioItemBinding


class PortfolioAdapter(private val context: Context, private val portfolioViewModel: PortfolioViewModel, private val rootView: View) :
    RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    // Method called by the item touch helper to delete a portfolio balance record
    fun onItemDismiss(position: Int) {

        portfolioViewModel.deleteRecord(position)
        notifyItemRemoved(position)
    }

    // Override the parent class's getItemCount method
    override fun getItemCount(): Int {
        return portfolioViewModel.getCount()
    }

    // Inflate the layout from XML and return the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {

        val binding =
            PortfolioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PortfolioViewHolder(binding)
    }

    // Populate data into the item through the holder
    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {

        // Get the name from the preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val name = preferences.getString("Individual", "")!!

        // Set item views based on the views and data model
        with (holder) {
            binding.textViewMonth.text = portfolioViewModel.getMonth(holder.adapterPosition)
            binding.textViewYear.text =
                portfolioViewModel.getYear(holder.adapterPosition).toString()
            binding.textViewBalance.text =
                String.format("%.2f", portfolioViewModel.getBalance(holder.adapterPosition))
        }

        // Set an on click listener on each earnings entry
        holder.itemView.setOnClickListener {

            val portfolioDialog = PortfolioDialog("update", rootView, portfolioViewModel, holder.adapterPosition,
                object: DialogCallback {
                    override fun dialogCallback() {

                        // Get the portfolio balance records from the database and update the adapter
                        portfolioViewModel.getPortfolioRecords(name)
                        notifyDataSetChanged()
                        MainActivity.showSnackBar(rootView, context.getString(R.string.update_complete))
                    }
                })
            portfolioDialog.show((context as AppCompatActivity).supportFragmentManager.beginTransaction(), "SOME_TAG")
        }
    }

    // Define the view holder as an inner class
    inner class PortfolioViewHolder(val binding: PortfolioItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
