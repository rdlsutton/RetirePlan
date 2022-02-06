package com.radlsuttonedmn.retireplan

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.radlsuttonedmn.retireplan.databinding.FragmentPortfolioValueBinding


/**
 * Graph fragment that displays a graph of the portfolio performance
 */

class PortfolioValueFragment : Fragment() {

    private lateinit var binding: FragmentPortfolioValueBinding
    private lateinit var portfolioViewModel: PortfolioViewModel

    // Override the parent class onCreateView method
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentPortfolioValueBinding.inflate(layoutInflater, container, false)
        val rootView = binding.root

        // Get the name from the preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val name = preferences.getString("Individual", "")!!

        // Connect to the data model resources
        portfolioViewModel = ViewModelProviders.of(this).get(PortfolioViewModel::class.java)
        portfolioViewModel.getPortfolioRecords(name)

        // Create a bitmap object to use as the canvas
        val graphBitmap = Bitmap.createBitmap(800, 1600, Bitmap.Config.ARGB_8888)
        val graphCanvas = Canvas(graphBitmap)

        // Create a paint object for drawing on the canvas
        val graphPaint = Paint()

        // Draw the portfolio performance graph
        drawGraph(name, portfolioViewModel, 0, graphCanvas, graphPaint)

        // Draw the graph on the canvas
        binding.imageViewPortfolioValue.setImageBitmap(graphBitmap)

        // Create an on touch listener to respond to settings buttons clicks
        binding.imageViewPortfolioValue.setOnTouchListener { _, event ->
            if (event!!.action == MotionEvent.ACTION_DOWN) {
                val x: Float = event.x
                val y: Float = event.y

                // Determine if one year button is clicked
                if (x > 135 && x < 185) {
                    if (y > 1285 && y < 1335) {
                        rootView.invalidate()
                        drawGraph(name, portfolioViewModel, 1, graphCanvas, graphPaint)
                    }
                }

                // Determine if five year button is clicked
                if (x > 275 && x < 325) {
                    if (y > 1285 && y < 1335) {
                        rootView.invalidate()
                        drawGraph(name, portfolioViewModel, 5, graphCanvas, graphPaint)
                    }
                }

                // Determine if ten year button is clicked
                if (x > 415 && x < 465) {
                    if (y > 1285 && y < 1335) {
                        rootView.invalidate()
                        drawGraph(name, portfolioViewModel, 10, graphCanvas, graphPaint)
                    }
                }

                // Determine if all button is clicked
                if (x > 555 && x < 605) {
                    if (y > 1285 && y < 1335) {
                        rootView.invalidate()
                        drawGraph(name, portfolioViewModel, 0, graphCanvas, graphPaint)
                    }
                }
            }

            requireView().performClick()
            true
        }

        return rootView
    }

    private fun drawGraph(name: String, portfolioList: PortfolioViewModel, graphSetting: Int, graphCanvas: Canvas, graphPaint: Paint) {

        // Set the background color
        graphCanvas.drawColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

        // Change the color of the virtual paint brush
        graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorWhite)

        // Set the line thickness
        graphPaint.strokeWidth = 4.0F

        // Draw the axis lines and tick marks
        drawAxisLines(graphSetting, graphCanvas, graphPaint)

        // Draw the dollar amounts on the vertical axis
        drawVerticalAxisAmounts(portfolioList, graphCanvas, graphPaint)

        // Draw the time periods on the horizontal axis
        drawHorizontalAxisPeriods(portfolioList, graphSetting, graphCanvas, graphPaint)

        // Graph the performance of the portfolio
        drawPerformanceLine(name, portfolioList, graphSetting, graphCanvas, graphPaint)

        // Draw the graph settings buttons
        drawSettingsButtons(graphSetting, graphCanvas, graphPaint)
    }

    private fun drawAxisLines(graphSetting: Int, graphCanvas: Canvas, graphPaint: Paint) {

        // Create the axis lines for the graph
        graphCanvas.drawLine(80.0F, 40.0F, 780.0F, 40.0F, graphPaint)
        graphCanvas.drawLine(80.0F, 40.0F, 80.0F, 1360.0F, graphPaint)
        graphCanvas.drawLine(80.0F, 1360.0F, 780.0F, 1360.0F, graphPaint)

        // Draw the tick marks on the first vertical axis
        graphCanvas.drawLine(780.0F, 20.0F, 780.0F, 40.0F, graphPaint)
        graphCanvas.drawLine(605.0F, 20.0F, 605.0F, 40.0F, graphPaint)
        graphCanvas.drawLine(430.0F, 20.0F, 430.0F, 40.0F, graphPaint)
        graphCanvas.drawLine(255.0F, 20.0F, 255.0F, 40.0F, graphPaint)

        // Draw the tick marks on the horizontal axis
        when (graphSetting) {
            1 -> {
                graphCanvas.drawLine(60.0F, 160.0F, 80.0F, 160.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 280.0F, 80.0F, 280.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 400.0F, 80.0F, 400.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 520.0F, 80.0F, 520.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 640.0F, 80.0F, 640.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 760.0F, 80.0F, 760.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 880.0F, 80.0F, 880.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 1000.0F, 80.0F, 1000.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 1120.0F, 80.0F, 1120.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 1240.0F, 80.0F, 1240.0F, graphPaint)
            }
            5 -> {
                graphCanvas.drawLine(60.0F, 304.0F, 80.0F, 304.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 568.0F, 80.0F, 568.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 832.0F, 80.0F, 832.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 1096.0F, 80.0F, 1096.0F, graphPaint)
            }
            10 -> {
                graphCanvas.drawLine(60.0F, 172.0F, 80.0F, 172.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 304.0F, 80.0F, 304.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 436.0F, 80.0F, 436.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 568.0F, 80.0F, 568.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 700.0F, 80.0F, 700.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 832.0F, 80.0F, 832.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 964.0F, 80.0F, 964.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 1096.0F, 80.0F, 1096.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 1228.0F, 80.0F, 1228.0F, graphPaint)
            }
            else -> {
                graphCanvas.drawLine(60.0F, 205.0F, 80.0F, 205.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 370.0F, 80.0F, 370.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 535.0F, 80.0F, 535.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 700.0F, 80.0F, 700.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 865.0F, 80.0F, 865.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 1030.0F, 80.0F, 1030.0F, graphPaint)
                graphCanvas.drawLine(60.0F, 1195.0F, 80.0F, 1195.0F, graphPaint)
            }
        }

        // Draw the tick marks on the second vertical axis
        graphCanvas.drawLine(780.0F, 1360.0F, 780.0F, 1380.0F, graphPaint)
        graphCanvas.drawLine(605.0F, 1360.0F, 605.0F, 1380.0F, graphPaint)
        graphCanvas.drawLine(430.0F, 1360.0F, 430.0F, 1380.0F, graphPaint)
        graphCanvas.drawLine(255.0F, 1360.0F, 255.0F, 1380.0F, graphPaint)
    }

    private fun drawVerticalAxisAmounts(portfolioList: PortfolioViewModel, graphCanvas: Canvas, graphPaint: Paint) {

        // Show the dollar amounts on the vertical axis
        if (portfolioList.getCount() > 1) {
            val currentBalance = portfolioList.getBalance(portfolioList.getCount() - 1)

            // Set the text size
            graphPaint.textSize = 32.0F

            // Determine the vertical axis values
            val valueOne = currentBalance / 4000
            val valueTwo = valueOne * 2
            val valueThree = valueOne * 3
            val valueFour = valueOne * 4

            // Draw the vertical axis values
            graphCanvas.save()
            graphCanvas.rotate(90f, 240.0F, 1400.0F)
            graphCanvas.drawText(String.format("%.0f", valueOne) + "k", 240.0F, 1400.0F, graphPaint)
            graphCanvas.restore()
            graphCanvas.save()
            graphCanvas.rotate(90f, 415.0F, 1400.0F)
            graphCanvas.drawText(String.format("%.0f", valueTwo) + "k", 415.0F, 1400.0F, graphPaint)
            graphCanvas.restore()
            graphCanvas.save()
            graphCanvas.rotate(90f, 590.0F, 1400.0F)
            graphCanvas.drawText(String.format("%.0f", valueThree) + "k", 590.0F, 1400.0F, graphPaint)
            graphCanvas.restore()
            graphCanvas.save()
            graphCanvas.rotate(90f, 765.0F, 1400.0F)
            graphCanvas.drawText(String.format("%.0f", valueFour) + "k", 765.0F, 1400.0F, graphPaint)
            graphCanvas.restore()
        }
    }

    private fun drawHorizontalAxisPeriods(portfolioList: PortfolioViewModel, graphSetting: Int, graphCanvas: Canvas, graphPaint: Paint) {

        if (portfolioList.getCount() > 1) {

            // Set up horizontal values
            val currentYear: Float
            val startYear: Float
            val labelOne: String
            val labelTwo: String
            val labelThree: String
            val labelFour: String
            val labelFive: String
            val labelSix: String
            var labelSeven = ""
            var labelEight = ""
            var labelNine = ""
            var labelTen = ""
            var labelEleven = ""
            var labelTwelve = ""

            // Determine the horizontal axis values
            when (graphSetting) {
                1 -> {
                    var currentMonth = (portfolioList.getMonthNumber(portfolioList.getCount() - 1))
                    val months: ArrayList<String> = ArrayList()
                    for (i in 1..12) {
                        months.add(getMonthAbbreviation(currentMonth))
                        currentMonth -= 1
                        if (currentMonth < 0) {
                            currentMonth = 11
                        }
                    }
                    labelOne = months[11]
                    labelTwo = months[10]
                    labelThree = months[9]
                    labelFour = months[8]
                    labelFive = months[7]
                    labelSix = months[6]
                    labelSeven = months[5]
                    labelEight = months[4]
                    labelNine = months[3]
                    labelTen = months[2]
                    labelEleven = months[1]
                    labelTwelve = months[0]
                }
                5 -> {
                    currentYear = (portfolioList.getYear(portfolioList.getCount() - 1)).toFloat()
                    startYear = currentYear - 5
                    labelOne = String.format("%.0f", startYear)
                    labelTwo = String.format("%.0f", startYear + 1)
                    labelThree = String.format("%.0f", startYear + 2)
                    labelFour = String.format("%.0f", startYear + 3)
                    labelFive = String.format("%.0f", startYear + 4)
                    labelSix = String.format("%.0f", currentYear)
                }
                10 -> {
                    currentYear = (portfolioList.getYear(portfolioList.getCount() - 1)).toFloat()
                    startYear = currentYear - 10
                    labelOne = String.format("%.0f", startYear)
                    labelTwo = String.format("%.0f", startYear + 1)
                    labelThree = String.format("%.0f", startYear + 2)
                    labelFour = String.format("%.0f", startYear + 3)
                    labelFive = String.format("%.0f", startYear + 4)
                    labelSix = String.format("%.0f", startYear + 5)
                    labelSeven = String.format("%.0f", startYear + 6)
                    labelEight = String.format("%.0f", startYear + 7)
                    labelNine = String.format("%.0f", startYear + 8)
                    labelTen = String.format("%.0f", startYear + 9)
                    labelEleven = String.format("%.0f", currentYear)
                }
                else -> {
                    currentYear = (portfolioList.getYear(portfolioList.getCount() - 1)).toFloat()
                    startYear = portfolioList.getYear(0).toFloat()
                    labelOne = String.format("%.0f", startYear)
                    labelTwo = String.format("%.0f", startYear + (currentYear - startYear)/8)
                    labelThree = String.format("%.0f", startYear + ((currentYear - startYear)/8) * 2)
                    labelFour = String.format("%.0f", startYear + ((currentYear - startYear)/8) * 3)
                    labelFive = String.format("%.0f", startYear + ((currentYear - startYear)/8) * 4)
                    labelSix = String.format("%.0f", startYear + ((currentYear - startYear)/8) * 5)
                    labelSeven = String.format("%.0f", startYear + ((currentYear - startYear)/8) * 6)
                    labelEight = String.format("%.0f", startYear + ((currentYear - startYear)/8) * 7)
                    labelNine = String.format("%.0f", currentYear)
                }
            }

            // Draw the horizontal axis values
            when (graphSetting) {
                1 -> {
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 5.0F)
                    graphCanvas.drawText(labelOne, 20.0F, 5.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 122.0F)
                    graphCanvas.drawText(labelTwo, 20.0F, 122.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 246.0F)
                    graphCanvas.drawText(labelThree, 20.0F, 246.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 367.0F)
                    graphCanvas.drawText(labelFour, 20.0F, 367.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 487.0F)
                    graphCanvas.drawText(labelFive, 20.0F, 487.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 606.0F)
                    graphCanvas.drawText(labelSix, 20.0F, 606.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 726.0F)
                    graphCanvas.drawText(labelSeven, 20.0F, 726.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 846.0F)
                    graphCanvas.drawText(labelEight, 20.0F, 846.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 966.0F)
                    graphCanvas.drawText(labelNine, 20.0F, 966.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 1086.0F)
                    graphCanvas.drawText(labelTen, 20.0F, 1086.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 1207.0F)
                    graphCanvas.drawText(labelEleven, 20.0F, 1207.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 1326.0F)
                    graphCanvas.drawText(labelTwelve, 20.0F, 1326.0F, graphPaint)
                    graphCanvas.restore()
                }
                5 -> {
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 5.0F)
                    graphCanvas.drawText(labelOne, 20.0F, 5.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 263.0F)
                    graphCanvas.drawText(labelTwo, 20.0F, 263.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 527.0F)
                    graphCanvas.drawText(labelThree, 20.0F, 527.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 791.0F)
                    graphCanvas.drawText(labelFour, 20.0F, 791.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 1055.0F)
                    graphCanvas.drawText(labelFive, 20.0F, 1055.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 1321.0F)
                    graphCanvas.drawText(labelSix, 20.0F, 1321.0F, graphPaint)
                    graphCanvas.restore()
                }
                10 -> {
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 5.0F)
                    graphCanvas.drawText(labelOne, 20.0F, 5.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 132.0F)
                    graphCanvas.drawText(labelTwo, 20.0F, 132.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 264.0F)
                    graphCanvas.drawText(labelThree, 20.0F, 264.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 396.0F)
                    graphCanvas.drawText(labelFour, 20.0F, 396.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 528.0F)
                    graphCanvas.drawText(labelFive, 20.0F, 528.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 660.0F)
                    graphCanvas.drawText(labelSix, 20.0F, 660.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 792.0F)
                    graphCanvas.drawText(labelSeven, 20.0F, 792.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 924.0F)
                    graphCanvas.drawText(labelEight, 20.0F, 924.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 1056.0F)
                    graphCanvas.drawText(labelNine, 20.0F, 1056.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 1188.0F)
                    graphCanvas.drawText(labelTen, 20.0F, 1188.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 1322.0F)
                    graphCanvas.drawText(labelEleven, 20.0F, 1322.0F, graphPaint)
                    graphCanvas.restore()
                }
                else -> {
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 5.0F)
                    graphCanvas.drawText(labelOne, 20.0F, 5.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 164.0F)
                    graphCanvas.drawText(labelTwo, 20.0F, 164.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 329.0F)
                    graphCanvas.drawText(labelThree, 20.0F, 329.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 494.0F)
                    graphCanvas.drawText(labelFour, 20.0F, 494.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 659.0F)
                    graphCanvas.drawText(labelFive, 20.0F, 659.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 824.0F)
                    graphCanvas.drawText(labelSix, 20.0F, 824.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 989.0F)
                    graphCanvas.drawText(labelSeven, 20.0F, 989.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 1154.0F)
                    graphCanvas.drawText(labelEight, 20.0F, 1154.0F, graphPaint)
                    graphCanvas.restore()
                    graphCanvas.save()
                    graphCanvas.rotate(90f, 20.0F, 1319.0F)
                    graphCanvas.drawText(labelNine, 20.0F, 1319.0F, graphPaint)
                    graphCanvas.restore()
                }
            }
        }
    }

    // Graph the performance of the portfolio
    private fun drawPerformanceLine(name: String, portfolioList: PortfolioViewModel, graphSetting: Int, graphCanvas: Canvas, graphPaint: Paint) {

        if (portfolioList.getCount() > 1) {

            val graphPoints: ArrayList<PortfolioRecord> = ArrayList()
            val currentMonth = portfolioList.getMonthNumber(portfolioList.getCount() - 1)
            val currentYear = portfolioList.getYear(portfolioList.getCount() - 1)
            when (graphSetting) {
                1 -> {
                    for (i in 1 until portfolioList.getCount()) {
                        if (portfolioList.getYear(i) == currentYear ||
                                        (portfolioList.getYear(i) == currentYear - 1 && portfolioList.getMonthNumber(i) >= currentMonth)) {
                            graphPoints.add(PortfolioRecord(name, portfolioList.getMonthNumber(i), portfolioList.getYear(i),
                                    portfolioList.getBalance(i)))
                        }
                    }
                }
                5 -> {
                    for (i in 1 until portfolioList.getCount()) {
                        if (portfolioList.getYear(i) >= currentYear - 4 ||
                                (portfolioList.getYear(i) == currentYear - 5 && portfolioList.getMonthNumber(i) >= currentMonth)) {
                            graphPoints.add(PortfolioRecord(name, portfolioList.getMonthNumber(i), portfolioList.getYear(i),
                                    portfolioList.getBalance(i)))
                        }
                    }
                }
                10 -> {
                    for (i in 1 until portfolioList.getCount()) {
                        if (portfolioList.getYear(i) >= currentYear - 9 ||
                                (portfolioList.getYear(i) == currentYear - 10 && portfolioList.getMonthNumber(i) >= currentMonth)) {
                            graphPoints.add(PortfolioRecord(name, portfolioList.getMonthNumber(i), portfolioList.getYear(i),
                                    portfolioList.getBalance(i)))
                        }
                    }
                }
                else -> {
                    for (i in 1 until portfolioList.getCount()) {
                        graphPoints.add(PortfolioRecord(name, portfolioList.getMonthNumber(i), portfolioList.getYear(i),
                                    portfolioList.getBalance(i)))
                    }
                }
            }

            val currentBalance = portfolioList.getBalance(portfolioList.getCount() - 1)

            // Compute the total number of months on the graph
            var numberOfMonths = 0.0F
            val firstRecord = graphPoints[0]
            val lastRecord = graphPoints[graphPoints.size - 1]
            if (lastRecord.year - firstRecord.year >= 1) {
                numberOfMonths = ((lastRecord.year - firstRecord.year - 1) * 12).toFloat()
            }
            numberOfMonths += (12 - firstRecord.month)
            numberOfMonths += lastRecord.month

            // Compute the number of pixels per month
            val startLocation = 40.0F
            val endLocation = 1360.0F
            val pixelsPerMonth: Float = (endLocation - startLocation) / numberOfMonths

            // Compute the number of dollars per pixel
            val topLocation = 780.0F
            val bottomLocation = 80.0F
            val dollarsPerPixel = (currentBalance / (topLocation - bottomLocation))

            // Plot the portfolio performance
            var startX: Float = (bottomLocation + firstRecord.balance / dollarsPerPixel).toFloat()
            var startY = startLocation

            for (i in 1 until graphPoints.size) {

                // Compute the number of months since portfolio
                var monthsSinceStart = 0.0F
                if (graphPoints[i].year == firstRecord.year) {
                    monthsSinceStart = (graphPoints[i].month - firstRecord.month).toFloat()
                } else {
                    if (graphPoints[i].year - firstRecord.year >= 1) {
                        monthsSinceStart = ((graphPoints[i].year - firstRecord.year - 1) * 12).toFloat()
                    }
                    monthsSinceStart += (12 - firstRecord.month)
                    monthsSinceStart += graphPoints[i].month
                }

                // Compute the end point for the graph line
                val endX = (bottomLocation + graphPoints[i].balance / dollarsPerPixel).toFloat()
                val endY = (startLocation + monthsSinceStart * pixelsPerMonth)

                // Draw the graph line
                graphCanvas.drawLine(startX, startY, endX, endY, graphPaint)

                // Set up the next line start location
                startX = endX
                startY = endY
            }
        }
    }

    // Draw the graph settings buttons
    private fun drawSettingsButtons( graphSetting: Int, graphCanvas: Canvas, graphPaint: Paint) {

        graphCanvas.save()
        graphCanvas.rotate(90f, 160.0F, 1520.0F)
        if (graphSetting == 1) {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorGreen)
        } else {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorRed)
        }
        graphCanvas.drawText(getString(R.string.one_year), 160.0F, 1520.0F, graphPaint)
        graphCanvas.restore()
        graphCanvas.save()
        graphCanvas.rotate(90f, 325.0F, 1520.0F)
        if (graphSetting == 5) {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorGreen)
        } else {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorRed)
        }
        graphCanvas.drawText(getString(R.string.five_year), 325.0F, 1520.0F, graphPaint)
        graphCanvas.restore()
        graphCanvas.save()
        graphCanvas.rotate(90f, 480.0F, 1520.0F)
        if (graphSetting == 10) {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorGreen)
        } else {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorRed)
        }
        graphCanvas.drawText(getString(R.string.ten_year), 480.0F, 1520.0F, graphPaint)
        graphCanvas.restore()
        graphCanvas.save()
        graphCanvas.rotate(90f, 640.0F, 1520.0F)
        if (graphSetting == 0) {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorGreen)
        } else {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorRed)
        }
        graphCanvas.drawText(getString(R.string.all), 640.0F, 1520.0F, graphPaint)
        graphCanvas.restore()
    }

    // Utility function to return a month abbreviation for a specified month number
    private fun getMonthAbbreviation(month: Int): String {

        return  when (month) {
            1 -> "Feb"
            2 -> "Mar"
            3 -> "Apr"
            4 -> "May"
            5 -> "Jun"
            6 -> "Jul"
            7 -> "Aug"
            8 -> "Sep"
            9 -> "Oct"
            10 -> "Nov"
            11 -> "Dec"
            else -> "Jan"
        }
    }
}
