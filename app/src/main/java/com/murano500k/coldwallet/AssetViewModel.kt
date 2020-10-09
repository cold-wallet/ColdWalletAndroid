package com.murano500k.coldwallet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.murano500k.coldwallet.assets.Asset
import com.murano500k.coldwallet.assets.AssetRepository
import com.murano500k.coldwallet.assets.AssetRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AssetViewModel(application: Application) : AndroidViewModel(application) ,
    AssetListAdapter.OnEditListener {

    private val repository: AssetRepository
    // Using LiveData and caching what getAssets returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allAssets: LiveData<List<Asset>>

    init {
        val assetsDao = AssetRoomDatabase.getDatabase(application, viewModelScope).assetDao()
        repository = AssetRepository(assetsDao)
        allAssets = repository.allAssets
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(asset: Asset) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(asset)
    }

    fun delete(asset: Asset) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(asset)
    }

    fun update(asset: Asset) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(asset)
    }

    override fun deleteButtonClicked(asset: Asset) {
        delete(asset)
    }

    override fun onValueChanged(asset: Asset) {
        update(asset)
    }
}