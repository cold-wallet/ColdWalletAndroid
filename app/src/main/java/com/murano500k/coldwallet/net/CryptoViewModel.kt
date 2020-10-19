package com.murano500k.coldwallet.net

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import com.murano500k.coldwallet.database.Asset
import com.murano500k.coldwallet.repo.Repository
import dagger.hilt.android.scopes.ActivityRetainedScoped

@ActivityRetainedScoped
class CryptoViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application)   {


    suspend fun initData(){
        repository.initData()
    }

    fun getAllAssetsInBTC(): ArrayList<Pair<Asset, Float>> {
        return getAllAssetsInCurrency("BTC")
    }

    fun getAllAssetsInUSDT(): ArrayList<Pair<Asset, Float>> {
        return getAllAssetsInCurrency("USDT")
    }


    fun getAllAssetsInCurrency(baseCurrency: String ): ArrayList<Pair<Asset, Float>> {
        val listPairs: ArrayList<Pair<Asset,Float>> = ArrayList()
        repository.getAllAssets().forEach { asset ->
            run {
                val rate = repository.getCryptoRate(asset.currency, baseCurrency)
                val resultAmount = asset.amount * rate
                listPairs.add(Pair(asset, resultAmount))
            }
        }
        return listPairs
    }

}