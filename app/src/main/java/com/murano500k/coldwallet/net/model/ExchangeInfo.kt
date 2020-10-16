package com.murano500k.coldwallet.net.model


import com.google.gson.annotations.SerializedName

data class ExchangeInfo(
    @SerializedName("exchangeFilters")
    val exchangeFilters: List<Filter> = listOf(),
    @SerializedName("rateLimits")
    val rateLimits: List<RateLimit> = listOf(),
    @SerializedName("serverTime")
    val serverTime: Long = 0,
    @SerializedName("symbols")
    val symbols: List<Symbol> = listOf(),
    @SerializedName("timezone")
    val timezone: String = ""
)