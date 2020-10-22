package com.murano500k.coldwallet.repo

import android.content.Context
import android.content.SharedPreferences
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
        const val ERROR_FLOAT = -1.0f
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
            if(cryptoCodeDao.getCryptoCodes().isEmpty()){
                val exchangeInfo = apiServiceBinance.getExchangeInfo()
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
            Log.e(TAG, "fetchCryptoCodes from network error", e);
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
                Log.w(TAG, "fetchCryptoPricePairs from network ");

                val prices = apiServiceBinance.getAllPrices()
                cryptoPricePairDao.insertAll(prices.map { priceItem -> CryptoPricePair(priceItem.symbol, priceItem.price) })
                Log.w(TAG, "fetchCryptoPricePairs from network ok");
            }else{
                Log.w(TAG, "fetchCryptoPricePairs from db ");
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
            if(getFiatPricePairs().isEmpty()){
                Log.w(TAG, "fetchFiatPricePairs from network");
                val prices = apiServiceMono.getFiatExchangePrices()
                insertFiatPricesToDb(prices)
                Log.w(TAG, "fetchFiatPricePairs from network ok");
            }else{
                Log.w(TAG, "fetchFiatPricePairs from db ");
                /*getFiatPricePairs().forEach {
                    Log.w(TAG, "fetchCryptoPricePairs ${it.currencyCodeA} ${it.currencyCodeB} ${it.rateCross}")
                }*/
            }
        }catch (e:Exception) {
            e.printStackTrace()
            Log.e(TAG, "fetchFiatPricePairs from network error", e);
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
            Log.w(TAG, "insertFiatPricesToDb ok size=${listDb.size} ");
            fiatPricePairDao.insertAll(listDb)
        }
    }
    private fun getCrossRate(rateBuy : Double, rateSell: Double): Double{
        return (rateBuy + rateSell) / 2
    }


}