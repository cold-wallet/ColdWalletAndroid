package com.murano500k.coldwallet.net.model


import com.google.gson.annotations.SerializedName

data class FiatExchangeInfoItem(
    @SerializedName("currencyCodeA")
    val currencyCodeA: Int = 0,
    @SerializedName("currencyCodeB")
    val currencyCodeB: Int = 0,
    @SerializedName("date")
    val date: Int = 0,
    @SerializedName("rateBuy")
    val rateBuy: Double = 0.0,
    @SerializedName("rateCross")
    val rateCross: Double = 0.0,
    @SerializedName("rateSell")
    val rateSell: Double = 0.0
)