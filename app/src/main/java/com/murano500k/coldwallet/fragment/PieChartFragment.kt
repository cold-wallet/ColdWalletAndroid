package com.murano500k.coldwallet.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.murano500k.coldwallet.activity.RatesActivity
import com.murano500k.coldwallet.net.utils.Status
import com.murano500k.coldwallet.viewmodel.RatesViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.fragment_pie_chart.*

@AndroidEntryPoint
@ActivityScoped
class PieChartFragment : Fragment(), OnChartValueSelectedListener {

    private val ratesViewModel: RatesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pie_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        ratesViewModel.fetchConvertedAssets("EUR", false)
    }



    private fun setupObserver() {
        ratesViewModel.getConvertedAssets().observe(viewLifecycleOwner, Observer {
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
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun initChartData(convertedAssets: List<ConvertedAsset>) {
        val pieValues = ArrayList<PieEntry>()
        convertedAssets.forEach { convertedAsset ->
            run {
                val label =
                    "${convertedAsset.baseAsset.currency} " +
                            "${convertedAsset.baseAsset.amount}"
                Log.w(RatesActivity.TAG, label )
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
        Log.w(RatesActivity.TAG, "pieData: ${pieValues.size}" );
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
        pieChart.description.text = "Rates in EUR"
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
        Log.w(RatesActivity.TAG, "onValueSelected: ${e.toString()}" );
    }

    override fun onNothingSelected() {
        Log.w(RatesActivity.TAG, "onNothingSelected" );
    }

    fun Float.roundTo(n : Int) : Float {
        return "%.${n}f".format(this).toFloat()
    }
}