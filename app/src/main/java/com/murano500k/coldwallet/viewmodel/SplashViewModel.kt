package com.murano500k.coldwallet.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.murano500k.coldwallet.net.utils.Resource
import com.murano500k.coldwallet.repo.Repository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ActivityRetainedScoped
class SplashViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    companion object {
        const val DELAY_MS = 1000L
    }
    private val status = MutableLiveData<Resource<String>>()

    fun fetchData() {
        viewModelScope.launch {
            status.postValue(Resource.loading("Loading"))
            delay(DELAY_MS)
            try {
                repository.initData()
                status.postValue(Resource.success("Success"))
            } catch (e: Exception) {
                e.printStackTrace()
                status.postValue(Resource.error("Something Went Wrong", null))
            }
        }
    }

    fun getStatus(): MutableLiveData<Resource<String>> {
        return status
    }
}