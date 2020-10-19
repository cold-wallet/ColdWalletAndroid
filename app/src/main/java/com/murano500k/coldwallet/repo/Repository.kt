package com.murano500k.coldwallet.repo

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.murano500k.coldwallet.database.*
import com.murano500k.coldwallet.net.api.ApiService
import com.murano500k.coldwallet.net.model.ExchangeInfo
import com.murano500k.coldwallet.net.model.Symbol
import javax.inject.Inject

class Repository @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val assetDao: AssetDao,
    private val cryptoCodeDao: CryptoCodeDao,
    private val cryptoPricePairDao: CryptoPricePairDao,
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        const val TAG = "Repository"
        const val ERROR_FLOAT = -1.0f
    }

    fun getAllAssets() = assetDao.getAssets()

    suspend fun getCryptoCodes() = cryptoCodeDao.getCryptoCodes().map { it.name }

    fun getCryptoPricePairs() = cryptoPricePairDao.getCryptoPricePairs()

    suspend fun initData(){
        fetchCryptoCodes()
        fetchCryptoPricePairs()
    }

    suspend fun fetchCryptoCodes() {
        try {
            if(cryptoCodeDao.getCryptoCodes().isEmpty()){
                val exchangeInfo = apiService.getExchangeInfo()
                Log.w(TAG, "fetchCryptoCodes from network  "+exchangeInfo.timezone);
                val listCodes = parseCryptoCodes(exchangeInfo)
                cryptoCodeDao.insertAll(listCodes.map{string ->
                    CryptoCode(string)
                })
            }else{
                Log.w(TAG, "fetchCryptoCodes from db ");
            }
        }catch (e:Exception) {
            e.printStackTrace()
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

    suspend fun fetchCryptoPricePairs(){
        try {
            if(getCryptoPricePairs().isEmpty()){
                val prices = apiService.getAllPrices()
                cryptoPricePairDao.insertAll(prices.map { priceItem -> CryptoPricePair(priceItem.symbol, priceItem.price) })
            }else{
                Log.w(TAG, "fetchCryptoCodes from db ");
            }
        }catch (e:Exception) {
            e.printStackTrace()
        }
    }


    fun getCryptoRate(rateFrom: String, rateTo: String) : Float {
        val symbol = "${rateFrom}${rateTo}"
        val symbolReverse = "${rateTo}${rateFrom}"
        if(rateFrom.equals(rateTo)){
            //smae currency
            return 1.0f
        }
        getCryptoPricePairs().forEach{item ->
            if(item.symbol.contains(symbol)) return item.price.toFloat()
            if(item.symbol.contains(symbolReverse)) return item.price.toFloat()
        }
        return ERROR_FLOAT
    }

    fun convertCrypto(amount: Float, rateFrom: String, rateTo: String): Float {
        val rate = getCryptoRate(rateFrom, rateTo)
        if(rate < 0) return ERROR_FLOAT
        else {
            return amount * rate
        }
    }
}