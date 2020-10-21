package com.murano500k.coldwallet.net.api

import com.murano500k.coldwallet.net.model.FiatExchangeInfoItem
import retrofit2.http.GET

interface ApiServiceMono {
    companion object {
        const val BASE_URL_MONO = "https://api.monobank.ua/"
    }

    @GET("/bank/currency/")
    suspend fun getFiatExchangePrices(): List<FiatExchangeInfoItem>
}