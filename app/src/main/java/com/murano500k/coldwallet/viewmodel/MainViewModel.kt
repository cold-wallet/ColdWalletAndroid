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
class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application)   {
    suspend fun initData(){
        repository.initData()

    }

    val allAssets: LiveData<List<Asset>> =repository.allAssets

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

}