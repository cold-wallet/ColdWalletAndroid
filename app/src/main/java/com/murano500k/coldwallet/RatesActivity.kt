package com.murano500k.coldwallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.getroadmap.lib.models.ForExRates
import com.getroadmap.lib.request.CurryApiClient
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RatesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rates)
        CurryApiClient(this).service.getLatestRates()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<ForExRates> {
                override fun onError(e: Throwable) {
                    Log.w("DEBUG", e.toString())
                }

                override fun onSuccess(t: ForExRates) {
                    Log.d("DEBUG", t.toString())
                    val usd_rate =t.rates.getRate("USD")
                    Log.d("DEBUG", "rate usd = $usd_rate")
                }

                override fun onSubscribe(d: Disposable) {
                }

            })

    }
}