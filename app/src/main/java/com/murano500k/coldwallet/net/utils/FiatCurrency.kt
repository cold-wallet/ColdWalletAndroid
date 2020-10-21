package com.murano500k.coldwallet.net.utils

data class FiatCurrency(
    val code: String,
    val numCode: Int,
    val afterDecimalPoint: Int,
    val name: String
)