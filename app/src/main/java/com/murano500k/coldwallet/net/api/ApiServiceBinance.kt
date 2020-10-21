package com.murano500k.coldwallet.net.api

import com.murano500k.coldwallet.net.model.ExchangeInfo
import com.murano500k.coldwallet.net.model.PriceItem
import retrofit2.http.GET

interface ApiServiceBinance {
    companion object {
        const val BASE_URL = "https://api.binance.com/"
    }

    @GET("api/v3/ticker/price")
    suspend fun getAllPrices(): List<PriceItem>
    @GET("api/v3/exchangeInfo")
    suspend fun getExchangeInfo(): ExchangeInfo
}