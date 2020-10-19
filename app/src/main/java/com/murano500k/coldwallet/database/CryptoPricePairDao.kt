package com.murano500k.coldwallet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CryptoPricePairDao {

    @Query("SELECT * from cryptopricepair_table ORDER BY symbol ASC")
    fun getCryptoPricePairs(): List<CryptoPricePair>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<CryptoPricePair>)

    @Query("DELETE FROM cryptopricepair_table")
    suspend fun deleteAll()
}