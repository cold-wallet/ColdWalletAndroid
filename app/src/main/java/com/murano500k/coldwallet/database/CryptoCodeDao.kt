package com.murano500k.coldwallet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CryptoCodeDao {

    @Query("SELECT * from cryptocode_table ORDER BY name ASC")
    suspend fun getCryptoCodes(): List<CryptoCode>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<CryptoCode>)

    @Query("DELETE FROM cryptocode_table")
    suspend fun deleteAll()
}