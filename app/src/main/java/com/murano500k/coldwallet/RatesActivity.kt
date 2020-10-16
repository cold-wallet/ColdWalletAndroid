package com.murano500k.coldwallet

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.getroadmap.lib.models.ForExRates
import com.getroadmap.lib.models.Rates
import com.getroadmap.lib.request.CurryApiClient
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.murano500k.coldwallet.db.assets.Asset
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_rates.*

class RatesActivity : AppCompatActivity(), OnChartValueSelectedListener {
    private lateinit var mPieValues: ArrayList<PieEntry>
    private lateinit var assetViewModel: AssetViewModel
    private lateinit var mAssets: List<Asset>
    private lateinit var mRates: Rates

    companion object{
        const val TAG = "RatesActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rates)
        assetViewModel = ViewModelProvider(this).get(AssetViewModel::class.java)
        assetViewModel.allAssets.observe(this, androidx.lifecycle.Observer { assets ->
            assets?.let { mAssets = assets }
        })
        CurryApiClient(this).service.getLatestRates("USD")
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<ForExRates> {
                override fun onError(e: Throwable) {
                    Log.w("DEBUG", e.toString())
                }

                override fun onSuccess(t: ForExRates) {
                    Log.d("DEBUG", t.toString())
                    mRates = t.rates
                    logRates(mRates)
                    val usd_rate =t.rates.getRate("USD")
                    Log.d("DEBUG", "rate usd = $usd_rate")
                    initPieValues()
                }

                override fun onSubscribe(d: Disposable) {

                }

            })

    }

    private fun logRates(mRates: Rates) {
        mRates.getRates().map{
            it.currency
        }
        mRates.getRates().forEach { Log.w(TAG, "logRates: $it" )}
    }

    private fun initPieValues(){
        mPieValues = ArrayList<PieEntry>()
        mAssets.forEach { asset ->
            run {
                val rate = mRates.getRate(asset.currency)
                Log.w(TAG, "rate:  $rate" );
                val resultValue = asset.amount / rate!!
                mPieValues.add(PieEntry(resultValue.toFloat(), "${asset.amount} ${asset.currency}"))
            }
        }
        initChart()
    }

    private fun initChart(){
        pieChart.setUsePercentValues(true)
        Log.w(TAG, "pieData: ${mPieValues.size}" );
        val dataSet = PieDataSet(mPieValues, "")
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