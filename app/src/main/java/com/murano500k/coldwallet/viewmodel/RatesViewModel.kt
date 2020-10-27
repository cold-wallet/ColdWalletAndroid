package com.murano500k.coldwallet.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.murano500k.coldwallet.components.Repository
import com.murano500k.coldwallet.components.TransormationHelper
import com.murano500k.coldwallet.model.ConvertedAsset
import com.murano500k.coldwallet.net.utils.Resource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.launch
import java.math.BigDecimal


@ActivityRetainedScoped
class RatesViewModel @ViewModelInject constructor(
    private val repository: Repository,
    private val transformationHelper : TransormationHelper,
    application: Application
): AndroidViewModel(application) {

    private val convertedAssets = MutableLiveData<Resource<List<ConvertedAsset>>>()

    private suspend fun getAssetAmountsInCurrency(requestedCurrencyCode: String,
                                                  isCrypto: Boolean): List<ConvertedAsset>{
        val resultList = ArrayList<ConvertedAsset>()
            repository.getAllAssets().forEach { asset ->
                run {
                    val amountInCurrency = transformationHelper.getAmountInOtherCurrency(
                        BigDecimal(asset.amount.toString()),
                        asset.currency,
                        asset.isCrypto,
                        requestedCurrencyCode,
                        isCrypto
                    )
                    resultList.add(
                        ConvertedAsset(
                            asset,
                            requestedCurrencyCode,
                            amountInCurrency,
                            isCrypto
                        )
                    )
                }
            }
        return resultList
    }

    fun fetchConvertedAssets(requestedCurrencyCode: String,
                                     isCrypto: Boolean) {
        viewModelScope.launch {
            convertedAssets.postValue(Resource.loading(null))
            try {
                convertedAssets.postValue(Resource.success(getAssetAmountsInCurrency(requestedCurrencyCode, isCrypto)))
            } catch (e: Exception) {
                e.printStackTrace()
                convertedAssets.postValue(Resource.error("Something Went Wrong", null))
            }
        }
    }

    fun getConvertedAssets(): MutableLiveData<Resource<List<ConvertedAsset>>> {
        return convertedAssets
    }
}
