package com.murano500k.coldwallet.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.murano500k.coldwallet.CURRENCY_TYPE
import com.murano500k.coldwallet.model.StatsItem
import com.murano500k.coldwallet.model.StatsRow
import com.murano500k.coldwallet.net.utils.Resource
import com.murano500k.coldwallet.components.Repository
import com.murano500k.coldwallet.components.TransormationHelper
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode


@ActivityRetainedScoped
class StatsViewModel @ViewModelInject constructor(
    private val repository: Repository,
    private val transformationHelper : TransormationHelper,
    application: Application
): AndroidViewModel(application) {

    private val stats = MutableLiveData<Resource<List<StatsItem>>>()



    private suspend fun generateStats(): List<StatsItem> {
        val resultList = ArrayList<StatsItem>()
        repository.getAllAssets().forEach {baseAsset ->
            val requestedCurrencyCode = baseAsset.currency
            val requestedIsCrypto = (baseAsset.type == CURRENCY_TYPE.CRYPTO.ordinal)
            var total = BigDecimal.ZERO
            val childList = ArrayList<StatsRow>()
            repository.getAllAssets().forEach { asset ->
                run {
                    if (!asset.currency.equals(requestedCurrencyCode)) {
                        val assetIsCrypto = (asset.type == CURRENCY_TYPE.CRYPTO.ordinal)
                        val amountInCurrency = transformationHelper.getAmountInOtherCurrency(
                            BigDecimal(asset.amount.toString()),
                            asset.currency,
                            assetIsCrypto,
                            requestedCurrencyCode,
                            requestedIsCrypto
                        )
                        total += amountInCurrency
                        childList.add(
                            StatsRow(
                                assetIsCrypto,
                                createRowText(
                                    asset.amount,
                                    asset.currency,
                                    amountInCurrency,
                                    requestedCurrencyCode
                                )
                            )
                        )
                    }
                }
            }
            resultList.add(
                StatsItem(
                    requestedCurrencyCode,
                    total,
                    childList
                )
            )
        }

        return resultList
    }

    private fun createRowText(
        amount: Float,
        currency: String,
        amountInCurrency: BigDecimal,
        requestedCurrencyCode: String
    ): String {
        val scaledAmonutInCurrency  = amountInCurrency.setScale(4, RoundingMode.CEILING)
        return "$amount $currency = $scaledAmonutInCurrency $requestedCurrencyCode"
    }

    fun fetchStats() {
        viewModelScope.launch {
            stats.postValue(Resource.loading(null))
            try {
                stats.postValue(Resource.success(generateStats()))
            } catch (e: Exception) {
                e.printStackTrace()
                stats.postValue(Resource.error("Something Went Wrong", null))
            }
        }
    }


    fun getStats(): MutableLiveData<Resource<List<StatsItem>>> {
        return stats
    }
}
