package com.murano500k.coldwallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.murano500k.coldwallet.database.DatabaseHelper
import com.murano500k.coldwallet.net.api.ApiHelper

class ViewModelFactory(private val apiHelper: ApiHelper, private val dbHelper: DatabaseHelper) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        /*if (modelClass.isAssignableFrom(CryptoViewModel::class.java)) {
            return CryptoViewModel(apiHelper) as T
        }*/
        throw IllegalArgumentException("Unknown class name")
    }

}