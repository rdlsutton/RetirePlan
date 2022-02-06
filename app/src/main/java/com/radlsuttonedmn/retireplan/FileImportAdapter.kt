package com.radlsuttonedmn.retireplan

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.radlsuttonedmn.retireplan.databinding.FileImportItemBinding


class FileImportAdapter(private val activity: Activity, private val importList: ArrayList<String>,
                        private val activityResult: ActivityResultLauncher<String>) :
    RecyclerView.Adapter<FileImportAdapter.FileImportViewHolder>() {

    // Override the parent class's getItemCount method
    override fun getItemCount(): Int {
        return importList.size
    }

    // Inflate the layout from XML and return the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileImportViewHolder {

        val binding = FileImportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileImportViewHolder(binding)
    }

    // Populate data into the item through the holder
    override fun onBindViewHolder(holder: FileImportViewHolder, position: Int) {

        // Set item views based on the views and data model
        holder.binding.textViewImportType.text = importList[holder.adapterPosition]

        // Set an on click listener on each earnings entry
        holder.itemView.setOnClickListener {

            // Set the import type based on which adapter position was selected
            when (importList[holder.adapterPosition]) {
                activity.getString(R.string.budget) -> FileImportFragment.importType = FileImportFragment.BUDGET
                activity.getString(R.string.life_expectancies) -> FileImportFragment.importType = FileImportFragment.LIFE_EXPECTANCY
                activity.getString(R.string.portfolio) -> FileImportFragment.importType = FileImportFragment.PORTFOLIO
                activity.getString(R.string.social_security) -> FileImportFragment.importType = FileImportFragment.SOCIAL_SECURITY
                activity.getString(R.string.tax_table) -> FileImportFragment.importType = FileImportFragment.TAX_TABLE
            }

            // Launch the get content activity for a result
            activityResult.launch("*/*")
        }
    }

    // Define the view holder as an inner class
    inner class FileImportViewHolder(val binding: FileImportItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
