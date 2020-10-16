package com.murano500k.coldwallet.net.model


import com.google.gson.annotations.SerializedName

data class Filter(
    @SerializedName("applyToMarket")
    val applyToMarket: Boolean = false,
    @SerializedName("avgPriceMins")
    val avgPriceMins: Int = 0,
    @SerializedName("filterType")
    val filterType: String = "",
    @SerializedName("limit")
    val limit: Int = 0,
    @SerializedName("maxNumAlgoOrders")
    val maxNumAlgoOrders: Int = 0,
    @SerializedName("maxNumOrders")
    val maxNumOrders: Int = 0,
    @SerializedName("maxPosition")
    val maxPosition: String = "",
    @SerializedName("maxPrice")
    val maxPrice: String = "",
    @SerializedName("maxQty")
    val maxQty: String = "",
    @SerializedName("minNotional")
    val minNotional: String = "",
    @SerializedName("minPrice")
    val minPrice: String = "",
    @SerializedName("minQty")
    val minQty: String = "",
    @SerializedName("multiplierDown")
    val multiplierDown: String = "",
    @SerializedName("multiplierUp")
    val multiplierUp: String = "",
    @SerializedName("stepSize")
    val stepSize: String = "",
    @SerializedName("tickSize")
    val tickSize: String = ""
)