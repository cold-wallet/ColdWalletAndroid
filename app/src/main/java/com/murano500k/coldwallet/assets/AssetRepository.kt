package com.murano500k.coldwallet.assets

import androidx.lifecycle.LiveData

class AssetRepository(private val assetDao: AssetDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allAssets: LiveData<List<Asset>> = assetDao.getAssets()

    suspend fun insert(asset: Asset) {
        assetDao.insert(asset)
    }
}