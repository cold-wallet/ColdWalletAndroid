package com.murano500k.coldwallet

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import com.murano500k.coldwallet.repo.Repository
import dagger.hilt.android.scopes.ActivityRetainedScoped


@ActivityRetainedScoped
class NewAssetViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application)   {
    suspend fun getCryptoCodes() = repository.getCryptoCodes()
}