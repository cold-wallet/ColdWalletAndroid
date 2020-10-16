package com.murano500k.coldwallet.net

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.murano500k.coldwallet.R
import com.murano500k.coldwallet.net.utils.Status

class CryptoActivity : AppCompatActivity() {
    companion object{
        const val TAG = "CryptoActivity"
    }
    private lateinit var cryptoViewModel: CryptoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto)
        setupViewModel()
        setupObserver()
    }

    private fun setupObserver() {
        cryptoViewModel.getPrices().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let{
                        Log.w(TAG, "SUCCESS loaded ${it.size} items" )
                    }
                }
                Status.LOADING -> {
                    Log.w(TAG, "LOADING" );
                }
                Status.ERROR -> {
                    Log.e(TAG, "ERROR ${it.message}" );
                }
            }
        })
        cryptoViewModel.getCryptoCodes().observe(this, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    resource.data?.let{
                        Log.w(TAG, "SUCCESS loaded ${it.size} items" )
                        it.forEach { Log.w(TAG, "loaded $it item" ) }
                    }
                }
                Status.LOADING -> {
                    Log.w(TAG, "LOADING" );
                }
                Status.ERROR -> {
                    Log.e(TAG, "ERROR ${resource.message}" );
                }
            }
        })
    }

    private fun setupViewModel() {
        cryptoViewModel = ViewModelProvider(this).get(CryptoViewModel::class.java)
    }
}
