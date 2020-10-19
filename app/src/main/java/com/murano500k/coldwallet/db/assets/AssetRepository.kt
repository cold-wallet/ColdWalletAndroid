package com.murano500k.coldwallet.db.assets

import androidx.lifecycle.LiveData
import com.murano500k.coldwallet.database.Asset
import com.murano500k.coldwallet.database.AssetDao

class AssetRepository(private val assetDao: AssetDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allAssets: LiveData<List<Asset>> = assetDao.getLiveAssets()

    suspend fun insert(asset: Asset) {
        assetDao.insert(asset)
    }
    suspend fun delete(asset: Asset) {
        assetDao.delete(asset)
    }
    suspend fun update(asset: Asset) {
        assetDao.update(asset)
    }

}