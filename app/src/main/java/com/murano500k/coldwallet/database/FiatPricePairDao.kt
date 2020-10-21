package com.murano500k.coldwallet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FiatPricePairDao {

    @Query("SELECT * from fiatpricepair_table ORDER BY currencyCodeA ASC")
    suspend fun getFiatPricePairs(): List<FiatPricePair>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<FiatPricePair>)

    @Query("DELETE FROM fiatpricepair_table")
    suspend fun deleteAll()
}