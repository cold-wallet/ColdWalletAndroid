package com.murano500k.coldwallet.net.model


import com.google.gson.annotations.SerializedName

data class RateLimit(
    @SerializedName("interval")
    val interval: String = "",
    @SerializedName("intervalNum")
    val intervalNum: Int = 0,
    @SerializedName("limit")
    val limit: Int = 0,
    @SerializedName("rateLimitType")
    val rateLimitType: String = ""
)