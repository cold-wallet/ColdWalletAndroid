package com.murano500k.coldwallet.net

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.murano500k.coldwallet.database.CryptoCode
import com.murano500k.coldwallet.database.CryptoCodeDao
import com.murano500k.coldwallet.db.assets.AssetRoomDatabase
import com.murano500k.coldwallet.net.api.ApiHelper
import com.murano500k.coldwallet.net.api.ApiHelperImpl
import com.murano500k.coldwallet.net.api.RetrofitBuilder
import com.murano500k.coldwallet.net.model.ExchangeInfo
import com.murano500k.coldwallet.net.model.PriceItem
import com.murano500k.coldwallet.net.model.Symbol
import com.murano500k.coldwallet.net.utils.Resource
import kotlinx.coroutines.launch

class CryptoViewModel(application: Application): AndroidViewModel(application)   {
    private val priceList = MutableLiveData<Resource<List<PriceItem>>>()
    private val cryptoCodesList = MutableLiveData<Resource<List<String>>>()
    private val cryptoCodeDao: CryptoCodeDao

    private val apiHelper: ApiHelper
    companion object{
        const val TAG = "CryptoViewModel"
    }
    init{
        cryptoCodeDao = AssetRoomDatabase
            .getDatabase(application.applicationContext, viewModelScope)
            .cryptoCodeDao()
        apiHelper = ApiHelperImpl(RetrofitBuilder.apiService)
        fetchPrices()
        fetchCryptoCodes()
    }

    private fun fetchPrices() {
        viewModelScope.launch {
            priceList.postValue(Resource.loading(null))
            try {
                val pricesFromApi = apiHelper.getAllPrices()
                priceList.postValue(Resource.success(pricesFromApi))
            }catch (e:Exception) {

                priceList.postValue(Resource.error(e.toString(),null))
            }
        }
    }
    fun getPrices(): LiveData<Resource<List<PriceItem>>>{
        return priceList
    }
    fun getCryptoCodes(): LiveData<Resource<List<String>>>{
        return cryptoCodesList
    }


    private fun fetchCryptoCodes() {
        cryptoCodesList.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                if(cryptoCodeDao.getCryptoCodes().isEmpty()){
                    val exchangeInfo = apiHelper.getExchangeInfo()
                    Log.w(TAG, "fetchExchangeInfo from network  "+exchangeInfo.timezone);
                    val listCodes = parseCryptoCodes(exchangeInfo)
                    cryptoCodeDao.insertAll(listCodes.map{string ->
                        CryptoCode(string)
                    })
                }else{
                    Log.w(TAG, "fetchExchangeInfo from db ");
                }
                cryptoCodesList.postValue(Resource.success(cryptoCodeDao.getCryptoCodes().map { it.name }))
            }catch (e:Exception) {
                e.printStackTrace()
                cryptoCodesList.postValue(Resource.error(e.toString(),null))
            }
        }
    }

    fun parseCryptoCodes(exchangeInfo: ExchangeInfo): ArrayList<String> {
        val listCurrencyCodes = ArrayList<String>()
        exchangeInfo.symbols.forEach {symbol: Symbol ->
            run {
                if (symbol.baseAsset !in listCurrencyCodes) {
                    listCurrencyCodes.add(symbol.baseAsset)
                }
            }
        }
        return listCurrencyCodes
    }
}