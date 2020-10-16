package com.murano500k.coldwallet.net.model

import com.google.gson.annotations.SerializedName

data class PriceItem (
    @SerializedName("symbol")
    val symbol: String = "",
    @SerializedName("price")
    val price: String = ""
)