package com.murano500k.coldwallet.database

interface DatabaseHelper {
    suspend fun getCryptoCodes(): List<CryptoCode>

    suspend fun insertAllCryptoCodes(codes: List<CryptoCode>)
}