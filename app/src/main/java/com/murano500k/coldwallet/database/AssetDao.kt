package com.murano500k.coldwallet.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AssetDao {

    @Query("SELECT * from asset_table ORDER BY currency ASC")
    fun getLiveAssets(): LiveData<List<Asset>>

    @Query("SELECT * from asset_table ORDER BY currency ASC")
    fun getAssets(): List<Asset>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asset: Asset)

    @Query("DELETE FROM asset_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(asset: Asset)

    @Update
    suspend fun update(asset: Asset)
}