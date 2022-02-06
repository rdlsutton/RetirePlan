package com.radlsuttonedmn.retireplan

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.radlsuttonedmn.retireplan.databinding.FragmentFileImportBinding
import java.util.*
import kotlin.collections.ArrayList

/**
 * Fragment that allows filling the database by importing CSV files
 */

class FileImportFragment : Fragment() {

    private lateinit var binding: FragmentFileImportBinding
    private lateinit var fileImportAdapter: FileImportAdapter
    private var importList = ArrayList<String>()

    // Override the parent class onCreateView method
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentFileImportBinding.inflate(layoutInflater, container, false)
        val rootView = binding.root

        // Initialize the import list
        importList.add(getString(R.string.budget))
        importList.add(getString(R.string.life_expectancies))
        importList.add(getString(R.string.portfolio))
        importList.add(getString(R.string.social_security))
        importList.add(getString(R.string.tax_table))

       // Add a list divider to the file import recyclerView
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.list_divider)!!)
        binding.recyclerViewFileImport.addItemDecoration(divider)

        // Register the callback for an Activity result
        val activityResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            importFile(uri!!, rootView)
        }
        // Create an adapter and pass in the file import list
        fileImportAdapter = FileImportAdapter(requireActivity(), importList, activityResult)

        // Attach the adapter to the recyclerView to populate the items
        binding.recyclerViewFileImport.adapter = fileImportAdapter

        // Set a layout manager to position the items
        binding.recyclerViewFileImport.layoutManager = LinearLayoutManager(requireContext())

        return rootView
    }

    private fun importFile(uri: Uri, rootView: View) {

        var results = getString(R.string.import_complete)
        // Process the selected input file
        try {
            val mime = MimeTypeMap.getSingleton()
            val fileType = mime.getExtensionFromMimeType(requireActivity().contentResolver?.getType(uri))
            if (fileType != null) {
                if (fileType.trim { it <= ' ' }.toLowerCase(Locale.getDefault()) == "csv") {
                    val fileImport = FileImport()
                    when (importType) {
                        BUDGET -> results = fileImport.importBudget(uri, requireContext())
                        LIFE_EXPECTANCY -> results = fileImport.importLifeExpectancies(uri, requireContext())
                        PORTFOLIO -> results = fileImport.importPortfolio(uri, requireContext())
                        SOCIAL_SECURITY -> results = fileImport.importSocialSecurity(uri, requireContext())
                        TAX_TABLE -> results = fileImport.importTaxTable(uri, requireContext())
                    }
                    MainActivity.showSnackBar(rootView, results)
                } else {
                    MainActivity.showSnackBar(rootView, getString(R.string.file_type))
                }
            }
        } catch (e: Exception) {
            MainActivity.showSnackBar(rootView, getString(R.string.file_read_error))
        }

        // If import was successful go to the affected fragment
        if (results == getString(R.string.import_complete)) {

            // Create a new fragment and specify the fragment to show based on file import selected
            var fragment: Fragment? = null
            val fragmentClass: Class<*> = when (importType) {
                BUDGET -> BudgetFragment::class.java
                LIFE_EXPECTANCY -> LifeExpectanciesFragment::class.java
                PORTFOLIO -> PortfolioFragment::class.java
                SOCIAL_SECURITY -> SocialSecurityFragment::class.java
                TAX_TABLE -> MainFragment::class.java
                else -> MainFragment::class.java
            }

            try {
                fragment = fragmentClass.newInstance() as Fragment
            } catch (e: java.lang.Exception) {
                MainActivity.showSnackBar(rootView, getString(R.string.app_fail))
            }

            // Insert the fragment by replacing any existing fragment
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, fragment!!).commit()
        }
    }

    companion object {
        const val BUDGET = 1
        const val LIFE_EXPECTANCY = 2
        const val PORTFOLIO = 3
        const val SOCIAL_SECURITY = 4
        const val TAX_TABLE = 5

        var importType: Int = BUDGET
    }
}
