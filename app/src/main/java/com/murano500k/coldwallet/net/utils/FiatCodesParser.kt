package com.murano500k.coldwallet.net.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.murano500k.coldwallet.net.model.FiatExchangeInfoItem
import java.io.IOException

class FiatCodesParser (val context: Context){
    lateinit var fiatCurrencies: List<FiatCurrency>
    lateinit var listFiatExchangeInfo: List<FiatExchangeInfoItem>
    val gson = Gson()
    companion object{
        const val TAG = "FiatCodesParser"
        const val ERROR_NAME = "ERROR"
        const val FILE_CURRENCIES_ISO4217 = "currencies-iso-4217.json"
        const val FILE_MONO_RESPONCE = "mono_responce_cache.json"
    }

    init{
        initFiatCurrencies()
        initMonoCache()
        //fiatCurrencies.forEachIndexed { idx, fiatCurrency -> Log.i("data", "> Item $idx:\n$fiatCurrency") }
    }

    private fun initMonoCache() {
        val jsonFileString = getJsonDataFromAsset(context, FILE_MONO_RESPONCE)
        val listFiatExchangeInfoType = object : TypeToken<List<FiatExchangeInfoItem>>() {}.type
        listFiatExchangeInfo = gson.fromJson(jsonFileString, listFiatExchangeInfoType)
    }

    private fun initFiatCurrencies(){
        val jsonFileString = getJsonDataFromAsset(context, "currencies-iso-4217.json")
        val listFiatCurrencyType = object : TypeToken<List<FiatCurrency>>() {}.type
        fiatCurrencies = gson.fromJson(jsonFileString, listFiatCurrencyType)
    }

    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    fun getCurrencyNameFromCode(code: Int): String {
        fiatCurrencies.forEach { if(it.numCode == code) return it.code }
        return ERROR_NAME
    }
}