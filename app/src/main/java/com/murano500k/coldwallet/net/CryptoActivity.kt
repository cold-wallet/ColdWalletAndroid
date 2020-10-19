package com.murano500k.coldwallet.net

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.murano500k.coldwallet.R
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
@ActivityScoped
class CryptoActivity : AppCompatActivity() {
    companion object{
        const val TAG = "CryptoActivity"
    }
    private val cryptoViewModel: CryptoViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto)

        lifecycleScope.launch(Dispatchers.IO){
            try{
                cryptoViewModel.initData()
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext, "data loaded", Toast.LENGTH_SHORT).show();
                }
                cryptoViewModel.getAllAssetsInUSDT().forEach{
                    Log.w(TAG, "${it.first.currency} in USDT = ${it.second}" );
                }
            }catch (e:Exception){
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext, "data load error", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }
}
