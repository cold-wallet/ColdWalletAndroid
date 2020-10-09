package com.murano500k.coldwallet

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.murano500k.coldwallet.Constants.EXTRA_CURRENCY
import com.murano500k.coldwallet.assets.Asset
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(),
    AssetListAdapter.OnEditListener {

    private lateinit var assetViewModel: AssetViewModel
    private val newAssetRequestCode = 1
    private val editAssetRequestCode = 2


    companion object {
        const val REQUEST_ADD_CURRENCY = 13
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)





        button_add.setOnClickListener {
            val intent = Intent(this@MainActivity, NewAssetActivity::class.java)
            startActivityForResult(intent, newAssetRequestCode)
        }
        button_rates.setOnClickListener {
            val intent = Intent(this@MainActivity, RatesActivity::class.java)
            startActivity(intent)
        }
        assetViewModel = ViewModelProvider(this).get(AssetViewModel::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.list_assets)
        val adapter = AssetListAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        assetViewModel.allAssets.observe(this, androidx.lifecycle.Observer { assets ->
            Log.w(TAG, "assets changed begin" );
            assets.forEach{
                Log.w(TAG, "assets changed $it" );
            }
            Log.w(TAG, "assets changed end" );
            assets?.let { adapter.setAssets(it) }
        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == newAssetRequestCode ){
            if (resultCode == Activity.RESULT_OK) {
                data?.getParcelableExtra<Asset>(NewAssetActivity.EXTRA_ASSET)?.let {
                    Log.w(TAG, "insert $it ");
                    assetViewModel.insert(it)
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG
                ).show()
            }
        }else if(requestCode == editAssetRequestCode){
            if (resultCode == Activity.RESULT_OK) {
                data?.getParcelableExtra<Asset>(NewAssetActivity.EXTRA_ASSET)?.let {
                    Log.w(TAG, "update $it ");
                    assetViewModel.update(it)
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    override fun deleteButtonClicked(asset: Asset) {
        assetViewModel.delete(asset)
    }

    override fun onEditClicked(asset: Asset) {
        val intent = Intent(this@MainActivity, NewAssetActivity::class.java)
        intent.putExtra(NewAssetActivity.EXTRA_ASSET,asset)
        startActivityForResult(intent, editAssetRequestCode)
    }

}