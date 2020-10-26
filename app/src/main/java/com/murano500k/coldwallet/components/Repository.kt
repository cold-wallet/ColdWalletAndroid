package com.murano500k.coldwallet.components

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import com.murano500k.coldwallet.database.*
import com.murano500k.coldwallet.net.api.ApiServiceBinance
import com.murano500k.coldwallet.net.api.ApiServiceMono
import com.murano500k.coldwallet.net.model.ExchangeInfo
import com.murano500k.coldwallet.net.model.FiatExchangeInfoItem
import com.murano500k.coldwallet.net.model.Symbol
import com.murano500k.coldwallet.net.utils.FiatCodesParser
import javax.inject.Inject

class Repository @Inject constructor(
    private val context: Context,
    private val apiServiceBinance: ApiServiceBinance,
    private val apiServiceMono: ApiServiceMono,
    private val assetDao: AssetDao,
    private val cryptoCodeDao: CryptoCodeDao,
    private val cryptoPricePairDao: CryptoPricePairDao,
    private val fiatPricePairDao: FiatPricePairDao,
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        const val TAG = "Repository"
        const val TWENTY_FOUR_HOURS = 86400000L
        const val NO_DATA = 0L
        const val TIMESTAMP_BINANCE = "TIMESTAMP_BINANCE"
        const val TIMESTAMP_MONO = "TIMESTAMP_MONO"
    }

    var timestampBinance: Long
        get() = sharedPreferences.getLong(TIMESTAMP_BINANCE, NO_DATA)
        set(t) = sharedPreferences.edit().putLong(TIMESTAMP_BINANCE, t).apply()

    var timestampMono: Long
        get() = sharedPreferences.getLong(TIMESTAMP_BINANCE, NO_DATA)
        set(t) = sharedPreferences.edit().putLong(TIMESTAMP_BINANCE, t).apply()

    private fun getTimeSinceLastUpdate(timestamp: Long): Long {
        if(timestamp == NO_DATA) return NO_DATA
        else return System.currentTimeMillis()- timestamp
    }

    private fun isDataStale(timestamp: Long): Boolean {
        return getTimeSinceLastUpdate(timestamp) > TWENTY_FOUR_HOURS
    }

    private fun isDataEmpty(timestamp: Long): Boolean {
        return getTimeSinceLastUpdate(timestamp) == NO_DATA
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allAssets: LiveData<List<Asset>> = assetDao.getLiveAssets()

    suspend fun insert(asset: Asset) {
        assetDao.insert(asset)
    }
    suspend fun delete(asset: Asset) {
        assetDao.delete(asset)
    }
    suspend fun update(asset: Asset) {
        assetDao.update(asset)
    }

    suspend fun getAllAssets() = assetDao.getAssets()

    suspend fun getCryptoCodes() = cryptoCodeDao.getCryptoCodes().map { it.name }

    fun getFiatCodes() = fiatCodesParser.fiatCurrencies.map{ it.code}

    suspend fun getCryptoPricePairs() = cryptoPricePairDao.getCryptoPricePairs()

    suspend fun getFiatPricePairs() = fiatPricePairDao.getFiatPricePairs()


    private val fiatCodesParser: FiatCodesParser

    init {
        fiatCodesParser = FiatCodesParser(context)
    }

    suspend fun initData(){
        fetchCryptoCodes()
        fetchCryptoPricePairs()
        fetchFiatPricePairs()
    }

    suspend fun fetchCryptoCodes() {
        try {
            if(isNetworkAvailable()){
                val exchangeInfo = apiServiceBinance.getExchangeInfo()
                Log.w(TAG, "fetchCryptoCodes from network  "+exchangeInfo.timezone)
                val listCodes = parseCryptoCodes(exchangeInfo)
                cryptoCodeDao.insertAll(listCodes.map{string ->
                    CryptoCode(string)
                })
            }
        }catch (e:Exception) {
            e.printStackTrace()
            Log.e(TAG, "fetchCryptoCodes from network error", e)
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
            if(isNetworkAvailable() && (isDataStale(timestampBinance) || isDataEmpty(timestampBinance))){
                Log.w(TAG, "fetchCryptoPricePairs from network ")

                val prices = apiServiceBinance.getAllPrices()
                timestampBinance = System.currentTimeMillis() * 1000L
                cryptoPricePairDao.insertAll(prices.map { priceItem -> CryptoPricePair(priceItem.symbol, priceItem.price) })
                Log.w(TAG, "fetchCryptoPricePairs from network ok")
            }else if(isDataEmpty(timestampBinance)){
                Log.w(TAG, "fetchCryptoPricePairs from db ")
                /*getCryptoPricePairs().forEach {
                    Log.w(TAG, "fetchCryptoPricePairs ${it.symbol} ${it.price}")
                }*/
            }
        }catch (e:Exception) {
            e.printStackTrace()
            Log.e(TAG, "fetchCryptoPricePairs from network error", e)
        }
    }



    suspend fun fetchFiatPricePairs(){
        try {
            if(isNetworkAvailable() && (isDataStale(timestampMono) || isDataEmpty(timestampMono))){
                Log.w(TAG, "fetchFiatPricePairs from network")
                val prices = apiServiceMono.getFiatExchangePrices()
                timestampMono = System.currentTimeMillis() * 1000L
                insertFiatPricesToDb(prices)
                Log.w(TAG, "fetchFiatPricePairs from network ok")
            }else{
                Log.w(TAG, "fetchFiatPricePairs from db ")
                /*getFiatPricePairs().forEach {
                    Log.w(TAG, "fetchCryptoPricePairs ${it.currencyCodeA} ${it.currencyCodeB} ${it.rateCross}")
                }*/
            }
        }catch (e:Exception) {
            e.printStackTrace()
            Log.e(TAG, "fetchFiatPricePairs from network error", e)
            insertFiatPricesToDb(fiatCodesParser.listFiatExchangeInfo)
        }
    }

    private suspend fun insertFiatPricesToDb(pricesFromApi: List<FiatExchangeInfoItem>) {
        var listDb = ArrayList<FiatPricePair>()
        var fiatCodesParser = FiatCodesParser(context)

        pricesFromApi.forEach { price ->
            run {
                var rateCross: Double = 0.0
                if (price.rateCross > 0) {
                    rateCross = price.rateCross
                }else if(price.rateBuy > 0 && price.rateSell > 0){
                    rateCross = getCrossRate(price.rateBuy, price.rateSell)
                }
                if(rateCross != 0.0) {
                    listDb.add(
                        FiatPricePair(
                            fiatCodesParser.getCurrencyNameFromCode(price.currencyCodeA),
                            fiatCodesParser.getCurrencyNameFromCode(price.currencyCodeB),
                            rateCross.toString()
                        )
                    )
                }
            }
        }
        if(listDb.size > 0){
            Log.w(TAG, "insertFiatPricesToDb ok size=${listDb.size} ")
            fiatPricePairDao.insertAll(listDb)
        }
    }
    private fun getCrossRate(rateBuy : Double, rateSell: Double): Double{
        return (rateBuy + rateSell) / 2
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected)
    }

}