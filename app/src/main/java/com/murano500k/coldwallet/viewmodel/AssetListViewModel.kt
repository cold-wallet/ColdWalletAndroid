package com.murano500k.coldwallet.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.murano500k.coldwallet.database.Asset
import com.murano500k.coldwallet.repo.Repository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ActivityRetainedScoped
class AssetListViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application) {
    val allAssets: LiveData<List<Asset>> =repository.allAssets

    fun delete(asset: Asset) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(asset)
    }
}