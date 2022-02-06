package com.radlsuttonedmn.retireplan

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.radlsuttonedmn.retireplan.databinding.FragmentPeriodGainsBinding

/**
 * Fragment that display the period gain results for the portfolio
 */

class PeriodGainsFragment : Fragment() {

    private lateinit var binding: FragmentPeriodGainsBinding
    private lateinit var portfolioViewModel: PortfolioViewModel
    private var periodGainsList = ArrayList<PeriodGainItem>()

    // Override the parent class onCreateView method
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentPeriodGainsBinding.inflate(layoutInflater, container, false)
        val rootView = binding.root

        // Get the name from the preferences
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val name = preferences.getString("Individual", "")!!

        // Connect to the data model resources
        portfolioViewModel = ViewModelProviders.of(this).get(PortfolioViewModel::class.java)
        portfolioViewModel.getPortfolioRecords(name)
        periodGainsList = portfolioViewModel.getPeriodGains(PeriodSetting.SIX_MONTH)

        // Create a bitmap object to use as the canvas
        val graphBitmap = Bitmap.createBitmap(800, 1600, Bitmap.Config.ARGB_8888)
        val graphCanvas = Canvas(graphBitmap)

        // Create a paint object for drawing on the canvas
        val graphPaint = Paint()

        // Draw the portfolio performance graph
        drawGraph( PeriodSetting.SIX_MONTH, graphCanvas, graphPaint)

        // Draw the graph on the canvas
        binding.imageViewPeriodGains.setImageBitmap(graphBitmap)

        // Create an on touch listener to respond to settings buttons clicks
        binding.imageViewPeriodGains.setOnTouchListener { _, event ->
            if (event!!.action == MotionEvent.ACTION_DOWN) {
                val x: Float = event.x
                val y: Float = event.y

                // Determine if twenty year button is clicked
                if (x > 40 && x < 145) {
                    if (y > 1285 && y < 1335) {
                        rootView.invalidate()
                        periodGainsList = portfolioViewModel.getPeriodGains(PeriodSetting.TWENTY_YEAR)
                        drawGraph( PeriodSetting.TWENTY_YEAR, graphCanvas, graphPaint)
                    }
                }

                // Determine if ten year button is clicked
                if (x > 145 && x < 270) {
                    if (y > 1285 && y < 1335) {
                        rootView.invalidate()
                        periodGainsList = portfolioViewModel.getPeriodGains(PeriodSetting.TEN_YEAR)
                        drawGraph( PeriodSetting.TEN_YEAR, graphCanvas, graphPaint)
                    }
                }

                // Determine if five year button is clicked
                if (x > 270 && x < 395) {
                    if (y > 1285 && y < 1335) {
                        rootView.invalidate()
                        periodGainsList = portfolioViewModel.getPeriodGains(PeriodSetting.FIVE_YEAR)
                        drawGraph( PeriodSetting.FIVE_YEAR, graphCanvas, graphPaint)
                    }
                }

                // Determine if two year button is clicked
                if (x > 395 && x < 520) {
                    if (y > 1285 && y < 1335) {
                        rootView.invalidate()
                        periodGainsList = portfolioViewModel.getPeriodGains(PeriodSetting.TWO_YEAR)
                        drawGraph(PeriodSetting.TWO_YEAR, graphCanvas, graphPaint)
                    }
                }

                // Determine if one year button is clicked
                if (x > 520 && x < 645) {
                    if (y > 1285 && y < 1335) {
                        rootView.invalidate()
                        periodGainsList = portfolioViewModel.getPeriodGains(PeriodSetting.ONE_YEAR)
                        drawGraph(PeriodSetting.ONE_YEAR, graphCanvas, graphPaint)
                    }
                }

                // Determine if six month button is clicked
                if (x > 645 && x < 790) {
                    if (y > 1285 && y < 1335) {
                        rootView.invalidate()
                        periodGainsList = portfolioViewModel.getPeriodGains(PeriodSetting.SIX_MONTH)
                        drawGraph(PeriodSetting.SIX_MONTH, graphCanvas, graphPaint)
                    }
                }
            }

            requireView().performClick()
            true
        }

        return rootView
    }

    private fun drawGraph(periodSetting: PeriodSetting, graphCanvas: Canvas, graphPaint: Paint) {

        // Set the background color
        graphCanvas.drawColor(ContextCompat.getColor(requireContext(), R.color.colorBlack))

        // Change the color of the virtual paint brush
        graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorWhite)

        // Set the line thickness
        graphPaint.strokeWidth = 4.0F

        // Draw the axis lines and tick marks
        drawAxisLines(graphCanvas, graphPaint)

        if (periodGainsList.size > 1) {
            // Draw the dollar amounts on the vertical axis
            drawVerticalAxisAmounts(graphCanvas, graphPaint)

            // Draw the time periods on the horizontal axis
            drawHorizontalAxisPeriods(graphCanvas, graphPaint)

            // Graph the performance of the portfolio
            drawPerformanceLine(graphCanvas, graphPaint)
        }

        // Draw the zero value line on the graph
        drawZeroLine(graphCanvas, graphPaint)

        // Draw the graph settings buttons
        drawSettingsButtons(periodSetting, graphCanvas, graphPaint)
    }

    private fun drawAxisLines(graphCanvas: Canvas, graphPaint: Paint) {

        // Create the axis lines for the graph
        graphCanvas.drawLine(80.0F, 40.0F, 780.0F, 40.0F, graphPaint)
        graphCanvas.drawLine(80.0F, 40.0F, 80.0F, 1360.0F, graphPaint)
        graphCanvas.drawLine(80.0F, 1360.0F, 780.0F, 1360.0F, graphPaint)

        // Draw the tick marks on the first vertical axis
        graphCanvas.drawLine(80.0F, 20.0F, 80.0F, 40.0F, graphPaint)
        graphCanvas.drawLine(255.0F, 20.0F, 255.0F, 40.0F, graphPaint)
        graphCanvas.drawLine(430.0F, 20.0F, 430.0F, 40.0F, graphPaint)
        graphCanvas.drawLine(605.0F, 20.0F, 605.0F, 40.0F, graphPaint)
        graphCanvas.drawLine(780.0F, 20.0F, 780.0F, 40.0F, graphPaint)

        // Draw the tick marks on the horizontal axis
        graphCanvas.drawLine(60.0F, 40.0F, 80.0F, 40.0F, graphPaint)
        graphCanvas.drawLine(60.0F, 205.0F, 80.0F, 205.0F, graphPaint)
        graphCanvas.drawLine(60.0F, 370.0F, 80.0F, 370.0F, graphPaint)
        graphCanvas.drawLine(60.0F, 535.0F, 80.0F, 535.0F, graphPaint)
        graphCanvas.drawLine(60.0F, 700.0F, 80.0F, 700.0F, graphPaint)
        graphCanvas.drawLine(60.0F, 865.0F, 80.0F, 865.0F, graphPaint)
        graphCanvas.drawLine(60.0F, 1030.0F, 80.0F, 1030.0F, graphPaint)
        graphCanvas.drawLine(60.0F, 1195.0F, 80.0F, 1195.0F, graphPaint)
        graphCanvas.drawLine(60.0F, 1360.0F, 80.0F, 1360.0F, graphPaint)

        // Draw the tick marks on the second vertical axis
        graphCanvas.drawLine(80.0F, 1360.0F, 80.0F, 1380.0F, graphPaint)
        graphCanvas.drawLine(255.0F, 1360.0F, 255.0F, 1380.0F, graphPaint)
        graphCanvas.drawLine(430.0F, 1360.0F, 430.0F, 1380.0F, graphPaint)
        graphCanvas.drawLine(605.0F, 1360.0F, 605.0F, 1380.0F, graphPaint)
        graphCanvas.drawLine(780.0F, 1360.0F, 780.0F, 1380.0F, graphPaint)
    }

    private fun drawVerticalAxisAmounts(graphCanvas: Canvas, graphPaint: Paint) {

        // Set the text size
        graphPaint.textSize = 32.0F

        // Determine the maximum and minimum gains
        val minGain: PeriodGainItem? = periodGainsList.minByOrNull { it.periodGainPercent }
        val maxGain: PeriodGainItem? = periodGainsList.maxByOrNull { it.periodGainPercent }

        // Determine the vertical axis values
        val valueOne = minGain!!.periodGainPercent
        val valueTwo = valueOne + (maxGain!!.periodGainPercent-minGain.periodGainPercent)/4
        val valueThree = valueTwo + (maxGain.periodGainPercent-minGain.periodGainPercent)/4
        val valueFour = valueThree + (maxGain.periodGainPercent-minGain.periodGainPercent)/4
        val valueFive = maxGain.periodGainPercent

        // Draw the vertical axis values
        graphCanvas.save()
        graphCanvas.rotate(90f, 65.0F, 1400.0F)
        graphCanvas.drawText(String.format("%.1f", valueOne) + "%", 65.0F, 1400.0F, graphPaint)
        graphCanvas.restore()
        graphCanvas.save()
        graphCanvas.rotate(90f, 240.0F, 1400.0F)
        graphCanvas.drawText(String.format("%.1f", valueTwo) + "%", 240.0F, 1400.0F, graphPaint)
        graphCanvas.restore()
        graphCanvas.save()
        graphCanvas.rotate(90f, 415.0F, 1400.0F)
        graphCanvas.drawText(String.format("%.1f", valueThree) + "%", 415.0F, 1400.0F, graphPaint)
        graphCanvas.restore()
        graphCanvas.save()
        graphCanvas.rotate(90f, 590.0F, 1400.0F)
        graphCanvas.drawText(String.format("%.1f", valueFour) + "%", 590.0F, 1400.0F, graphPaint)
        graphCanvas.restore()
        graphCanvas.save()
        graphCanvas.rotate(90f, 765.0F, 1400.0F)
        graphCanvas.drawText(String.format("%.1f", valueFive) + "%", 765.0F, 1400.0F, graphPaint)
        graphCanvas.restore()
    }

    private fun drawHorizontalAxisPeriods(graphCanvas: Canvas, graphPaint: Paint) {

        // Determine the horizontal axis values
        val currentYear: Float = (periodGainsList[periodGainsList.size - 1].year).toFloat()
        val startYear: Float = periodGainsList[0].year.toFloat()
        val labelOne:String  = String.format("%.0f", startYear)
        val labelTwo:String = String.format("%.0f", startYear + (currentYear - startYear)/8)
        val labelThree:String = String.format("%.0f", startYear + ((currentYear - startYear)/8) * 2)
        val labelFour:String = String.format("%.0f", startYear + ((currentYear - startYear)/8) * 3)
        val labelFive:String = String.format("%.0f", startYear + ((currentYear - startYear)/8) * 4)
        val labelSix:String = String.format("%.0f", startYear + ((currentYear - startYear)/8) * 5)
        val labelSeven:String = String.format("%.0f", startYear + ((currentYear - startYear)/8) * 6)
        val labelEight:String = String.format("%.0f", startYear + ((currentYear - startYear)/8) * 7)
        val labelNine:String = String.format("%.0f", currentYear)

        // Draw the horizontal axis values
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

    // Graph the performance of the portfolio
    private fun drawPerformanceLine(graphCanvas: Canvas, graphPaint: Paint) {

        val minGain: PeriodGainItem? = periodGainsList.minByOrNull { it.periodGainPercent }
        val maxGain: PeriodGainItem? = periodGainsList.maxByOrNull { it.periodGainPercent }
        val gainRange = maxGain!!.periodGainPercent - minGain!!.periodGainPercent

        // Compute the total number of months on the graph
        var numberOfMonths = 0.0F
        val firstRecord = periodGainsList[0]
        val lastRecord = periodGainsList[periodGainsList.size - 1]
        if (lastRecord.year - firstRecord.year >= 1) {
            numberOfMonths = ((lastRecord.year - firstRecord.year - 1) * 12).toFloat()
        }
        numberOfMonths += (12 - getMonthNumber(firstRecord.month))
        numberOfMonths += getMonthNumber(lastRecord.month)

        // Compute the number of pixels per month
        val startLocation = 40.0F
        val endLocation = 1360.0F
        val pixelsPerMonth: Float = (endLocation - startLocation) / numberOfMonths

        // Compute the amount of gain per pixel
        val topLocation = 780.0F
        val bottomLocation = 80.0F
        val gainPerPixel = (gainRange / (topLocation - bottomLocation))

        // Plot the portfolio performance
        var startX: Float = (bottomLocation + firstRecord.periodGainPercent / gainPerPixel).toFloat()
        var startY = startLocation

        for (i in 1 until periodGainsList.size) {

            // Compute the number of months since portfolio
            var monthsSinceStart = 0.0F
            if (periodGainsList[i].year == firstRecord.year) {
                monthsSinceStart = (getMonthNumber(periodGainsList[i].month) - getMonthNumber(firstRecord.month)).toFloat()
            } else {
                if (periodGainsList[i].year - firstRecord.year >= 1) {
                    monthsSinceStart = ((periodGainsList[i].year - firstRecord.year - 1) * 12).toFloat()
                }
                monthsSinceStart += (12 - getMonthNumber(firstRecord.month))
                monthsSinceStart += getMonthNumber(periodGainsList[i].month)
            }

            // Compute the end point for the graph line
            val plotAmount = periodGainsList[i].periodGainPercent + kotlin.math.abs(minGain.periodGainPercent)
            val endX = (bottomLocation + (plotAmount / gainPerPixel)).toFloat()
            val endY = (startLocation + monthsSinceStart * pixelsPerMonth)

            // Draw the graph line
            graphCanvas.drawLine(startX, startY, endX, endY, graphPaint)

            // Set up the next line start location
            startX = endX
            startY = endY
        }
    }

    // Draw the zero value line on the graph
    private fun drawZeroLine(graphCanvas: Canvas, graphPaint: Paint) {

        val minGain: PeriodGainItem? = periodGainsList.minByOrNull { it.periodGainPercent }

        // Do not show a line unless the minimum gain is below a negative two percent
        if (minGain!!.periodGainPercent < -2.0F) {

            val maxGain: PeriodGainItem? = periodGainsList.maxByOrNull { it.periodGainPercent }
            val gainRange = maxGain!!.periodGainPercent - minGain.periodGainPercent

            // Compute the amount of gain per pixel
            val topLocation = 780.0F
            val bottomLocation = 80.0F
            val gainPerPixel = (gainRange / (topLocation - bottomLocation))

            // Compute the x coordinate for the zero line
             val lineX = (bottomLocation + (kotlin.math.abs(minGain.periodGainPercent) / gainPerPixel)).toFloat()

            // Draw the graph line
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorRed)
            graphCanvas.save()
            graphCanvas.rotate(90f, lineX - 10.0F, 1380.0F)
            graphCanvas.drawText("0%", lineX - 10.0F, 1380.0F, graphPaint)
            graphCanvas.restore()
            graphCanvas.drawLine(lineX, 40.0F, lineX, 1360.0F, graphPaint)
        }
    }

    // Draw the graph settings buttons
    private fun drawSettingsButtons(periodSetting: PeriodSetting, graphCanvas: Canvas, graphPaint: Paint) {

        graphCanvas.save()
        graphCanvas.rotate(90f, 65.0F, 1520.0F)
        if (periodSetting == PeriodSetting.TWENTY_YEAR) {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorGreen)
        } else {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorRed)
        }
        graphCanvas.drawText(getString(R.string.twenty_year), 65.0F, 1520.0F, graphPaint)
        graphCanvas.restore()
        graphCanvas.save()
        graphCanvas.rotate(90f, 205.0F, 1520.0F)
        if (periodSetting == PeriodSetting.TEN_YEAR) {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorGreen)
        } else {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorRed)
        }
        graphCanvas.drawText(getString(R.string.ten_year), 205.0F, 1520.0F, graphPaint)
        graphCanvas.restore()
        graphCanvas.save()
        graphCanvas.rotate(90f, 345.0F, 1520.0F)
        if (periodSetting == PeriodSetting.FIVE_YEAR) {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorGreen)
        } else {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorRed)
        }
        graphCanvas.drawText(getString(R.string.five_year), 345.0F, 1520.0F, graphPaint)
        graphCanvas.restore()
        graphCanvas.save()
        graphCanvas.rotate(90f, 485.0F, 1520.0F)
        if (periodSetting == PeriodSetting.TWO_YEAR) {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorGreen)
        } else {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorRed)
        }
        graphCanvas.drawText(getString(R.string.two_year), 485.0F, 1520.0F, graphPaint)
        graphCanvas.restore()
        graphCanvas.save()
        graphCanvas.rotate(90f, 625.0F, 1520.0F)
        if (periodSetting == PeriodSetting.ONE_YEAR) {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorGreen)
        } else {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorRed)
        }
        graphCanvas.drawText(getString(R.string.one_year), 625.0F, 1520.0F, graphPaint)
        graphCanvas.restore()
        graphCanvas.save()
        graphCanvas.rotate(90f, 765.0F, 1520.0F)
        if (periodSetting == PeriodSetting.SIX_MONTH) {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorGreen)
        } else {
            graphPaint.color = ContextCompat.getColor(requireContext(), R.color.colorRed)
        }
        graphCanvas.drawText(getString(R.string.six_month), 765.0F, 1520.0F, graphPaint)
        graphCanvas.restore()
    }

    private fun getMonthNumber(month: String): Int {
        return when (month) {
            "February" -> 1
            "March" -> 2
            "April" -> 3
            "May" -> 4
            "June" -> 5
            "July" -> 6
            "August" -> 7
            "September" -> 8
            "October" -> 9
            "November" -> 10
            "December" -> 11
            else -> 0
        }
    }
}
