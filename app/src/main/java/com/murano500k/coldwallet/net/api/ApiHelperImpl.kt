package com.murano500k.coldwallet.net.api

class ApiHelperImpl(private val apiService: ApiService): ApiHelper {
    override suspend fun getAllPrices() = apiService.getAllPrices()
    override suspend fun getExchangeInfo() = apiService.getExchangeInfo()

}