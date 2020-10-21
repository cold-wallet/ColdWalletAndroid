package com.murano500k.coldwallet.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.murano500k.coldwallet.ConvertedAsset
import com.murano500k.coldwallet.R
import com.murano500k.coldwallet.net.utils.Status
import com.murano500k.coldwallet.viewmodel.RatesViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.activity_all_rates.*

@AndroidEntryPoint
@ActivityScoped
class RatesActivity: AppCompatActivity(), OnChartValueSelectedListener {

    companion object {
        const val TAG = "AllRatesActivity"
    }

    private val ratesViewModel: RatesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.setHomeButtonEnabled(true)
        setContentView(R.layout.activity_all_rates)
        setupObserver()
        setupButton()
    }

    private fun setupButton() {
        fabRefresh.setOnClickListener{
            ratesViewModel.fetchConvertedAssets("EUR", false)
        }
    }


    private fun setupObserver() {
        ratesViewModel.getConvertedAssets().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    it.data?.let { convertedAssets -> initChartData(convertedAssets) }
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun initChartData(convertedAssets: List<ConvertedAsset>) {
        val pieValues = ArrayList<PieEntry>()
        convertedAssets.forEach { convertedAsset ->
            run {
                val label = "rate: ${convertedAsset.baseAsset.currency} ${convertedAsset.baseAsset.amount}  in ${convertedAsset.convertedCurrencyCode} = ${convertedAsset.convertedAmount}"

                Log.w(TAG, label )
                //val resultValue = asset.amount / rate!!
                pieValues.add(
                    PieEntry(
                        convertedAsset.convertedAmount.toFloat(),
                        label
                    )
                )
            }
        }
        initChart(pieValues)

    }


    private fun initChart(pieValues: List<PieEntry>){
        pieChart.setUsePercentValues(true)
        Log.w(TAG, "pieData: ${pieValues.size}" );
        val dataSet = PieDataSet(pieValues, "")
        dataSet.isHighlightEnabled = true
        val data = PieData(dataSet)

        //pieChartColor(data,dataSet)
        // In Percentage
        data.setValueFormatter(object : ValueFormatter(){
            override fun getFormattedValue(value: Float): String? {

                return "${value.roundTo(2).toString()}%"
            }
        })
        pieChart.data = data
        pieChart.description.text = "Rates in USD"
        pieChart.isDrawHoleEnabled = false
        //data.setValueTextSize(13f)

        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 10f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 10f
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)


        data.setValueTextSize(20f)
        data.setValueTextColor(Color.WHITE)

        pieChart.highlightValues(null)
        pieChart.invalidate()
        pieChart.animateXY(2000, 2000)
        pieChart.setEntryLabelTextSize(20f)
        pieChart.setOnChartValueSelectedListener(this)
        //chartDetails(pieChart, Typeface.SANS_SERIF)
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        Log.w(TAG, "onValueSelected: ${e.toString()}" );
    }

    override fun onNothingSelected() {
        Log.w(TAG, "onNothingSelected" );
    }

    fun Float.roundTo(n : Int) : Float {
        return "%.${n}f".format(this).toFloat()
    }
}