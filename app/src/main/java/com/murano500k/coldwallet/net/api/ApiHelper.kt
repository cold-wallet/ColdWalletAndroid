package com.murano500k.coldwallet.net.api

import com.murano500k.coldwallet.net.model.ExchangeInfo
import com.murano500k.coldwallet.net.model.PriceItem

interface ApiHelper {
    suspend fun getAllPrices() : List<PriceItem>
    suspend fun getExchangeInfo(): ExchangeInfo
}