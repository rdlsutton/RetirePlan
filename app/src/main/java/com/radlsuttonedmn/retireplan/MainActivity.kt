package com.radlsuttonedmn.retireplan

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout
import com.radlsuttonedmn.retireplan.databinding.ActivityMainBinding
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    // Variables needed for the navigation drawer
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var rootView: View
    private var onMainFragment: Boolean = true

    // Override the parent class onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        rootView = binding.root
        setContentView(rootView)

        // Set up the retire plan room database
        retirePlanDatabase = Room.databaseBuilder(
            applicationContext,
            RetirePlanDatabase::class.java, "RetirePlanDatabase"
        )
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

        //fillEarnings()
        //fillPortfolio()
        //fillExpectancies()
        //fillTaxTable()

        // Add a tool bar to use as an action bar at the top of the screen
        setSupportActionBar(binding.appToolbar)

        // Connect to the navigation drawer
        drawer = binding.layoutMain

        // Set up the navigation drawer
        toggle = ActionBarDrawerToggle(
            this, drawer, binding.appToolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Initialize the navigation view and set its listener
        binding.navigationView.setNavigationItemSelectedListener(this)

        // Set an on click listener on the home button
        binding.buttonHome.setOnClickListener {
            if (onMainFragment) {
                finishAffinity()
                exitProcess(0)
            } else {

                // Return to the main fragment
                onMainFragment = true
                val newFragment: Fragment = MainFragment()
                val transaction = this.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.nav_host_fragment, newFragment).commit()
            }
        }
    }

    // Override the parent class onPostCreate method
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    // Override the parent class onConfigurationChanged method
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    // Go to the proper fragment when drawer option is selected
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Create a new fragment and specify the fragment to show based on nav item clicked
        var fragment: Fragment? = null
        onMainFragment = false
        val fragmentClass: Class<*> = when (item.itemId) {
            R.id.home -> MainFragment::class.java
            R.id.settings -> SettingsFragment::class.java
            R.id.social_security -> SocialSecurityFragment::class.java
            R.id.portfolio -> PortfolioFragment::class.java
            R.id.overall_gains -> OverallGainsFragment::class.java
            R.id.period_gains -> PeriodGainsFragment::class.java
            R.id.expectancies -> LifeExpectanciesFragment::class.java
            R.id.budget -> BudgetFragment::class.java
            R.id.portfolio_value -> PortfolioValueFragment::class.java
            R.id.file_import -> FileImportFragment::class.java
            R.id.snapshot -> SnapshotFragment::class.java
            else -> MainFragment::class.java
        }

        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: java.lang.Exception) {
            showSnackBar(rootView, getString(R.string.app_fail))
        }

        // Insert the fragment by replacing any existing fragment
        val transaction = this.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment!!).commit()

        // Close the navigation drawer
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    // When back arrow pressed, close the drawer if it is opened
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Handle the menu option selection events
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        var retirePlanDatabase: RetirePlanDatabase? = null

        // Convert a text string to a bitmap
        fun textAsBitmap(text: String, context: Context): Bitmap? {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.textSize = 40.0F
            paint.color = getColor(context, R.color.colorWhite)
            paint.textAlign = Paint.Align.LEFT
            val baseline: Float = -paint.ascent() // ascent() is negative
            val width = (paint.measureText(text) + 0.0f).toInt() // round
            val height = (baseline + paint.descent() + 0.0f).toInt()
            val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(image)
            canvas.drawText(text, 0.0F, baseline, paint)
            return image
        }

        // Show a snack bar with updated colors for the background and text
        fun showSnackBar(rootView: View, message: String?) {
            val snackBar = Snackbar.make(rootView, message!!, Snackbar.LENGTH_LONG)
            val view: View = snackBar.view
            val backgroundColor =
                    ResourcesCompat.getColor(view.resources, R.color.colorBlack, null)
            view.setBackgroundColor(backgroundColor)
            val snackBarMessage: TextView =
                    view.findViewById(R.id.snackbar_text)
            snackBarMessage.setTextColor(ResourcesCompat.getColor(view.resources, R.color.colorWhite, null))
            (snackBarMessage.parent as SnackbarContentLayout).setBackgroundColor(backgroundColor)
            snackBarMessage.textAlignment = View.TEXT_ALIGNMENT_CENTER
            snackBar.show()
        }
    }
}
