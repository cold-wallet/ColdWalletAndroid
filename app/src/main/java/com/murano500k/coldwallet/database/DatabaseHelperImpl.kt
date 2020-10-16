package com.murano500k.coldwallet.database

import com.murano500k.coldwallet.db.assets.AssetRoomDatabase

class DatabaseHelperImpl(private val appDatabase: AssetRoomDatabase) : DatabaseHelper {
    override suspend fun getCryptoCodes(): List<CryptoCode>
            = appDatabase.cryptoCodeDao().getCryptoCodes()

    override suspend fun insertAllCryptoCodes(codes: List<CryptoCode>)
            = appDatabase.cryptoCodeDao().insertAll(codes)

}