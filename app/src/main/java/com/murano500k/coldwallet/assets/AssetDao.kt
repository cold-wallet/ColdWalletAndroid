package com.murano500k.coldwallet.assets

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AssetDao {

    @Query("SELECT * from asset_table ORDER BY currency ASC")
    fun getAssets(): LiveData<List<Asset>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(asset: Asset)

    @Query("DELETE FROM asset_table")
    suspend fun deleteAll()
}