package com.murano500k.coldwallet.net.model


import com.google.gson.annotations.SerializedName

data class Symbol(
    @SerializedName("baseAsset")
    val baseAsset: String = "",
    @SerializedName("baseAssetPrecision")
    val baseAssetPrecision: Int = 0,
    @SerializedName("baseCommissionPrecision")
    val baseCommissionPrecision: Int = 0,
    @SerializedName("filters")
    val filters: List<Filter> = listOf(),
    @SerializedName("icebergAllowed")
    val icebergAllowed: Boolean = false,
    @SerializedName("isMarginTradingAllowed")
    val isMarginTradingAllowed: Boolean = false,
    @SerializedName("isSpotTradingAllowed")
    val isSpotTradingAllowed: Boolean = false,
    @SerializedName("ocoAllowed")
    val ocoAllowed: Boolean = false,
    @SerializedName("orderTypes")
    val orderTypes: List<String> = listOf(),
    @SerializedName("permissions")
    val permissions: List<String> = listOf(),
    @SerializedName("quoteAsset")
    val quoteAsset: String = "",
    @SerializedName("quoteAssetPrecision")
    val quoteAssetPrecision: Int = 0,
    @SerializedName("quoteCommissionPrecision")
    val quoteCommissionPrecision: Int = 0,
    @SerializedName("quoteOrderQtyMarketAllowed")
    val quoteOrderQtyMarketAllowed: Boolean = false,
    @SerializedName("quotePrecision")
    val quotePrecision: Int = 0,
    @SerializedName("status")
    val status: String = "",
    @SerializedName("symbol")
    val symbol: String = ""
)