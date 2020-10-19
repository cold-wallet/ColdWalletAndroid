package com.murano500k.coldwallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.murano500k.coldwallet.database.Asset
import com.murano500k.coldwallet.net.CryptoActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
@ActivityScoped
class MainActivity : AppCompatActivity(),
    AssetListAdapter.OnEditListener {

    private lateinit var assetViewModel: AssetViewModel
    private val newAssetRequestCode = 1
    private val editAssetRequestCode = 2
    private val mainViewModel: MainViewModel by viewModels()

    companion object {
        const val REQUEST_ADD_CURRENCY = 13
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initButtons()
        assetViewModel = ViewModelProvider(this).get(AssetViewModel::class.java)
        initListView()
        fetchData()


    }

    private fun fetchData() {
        setButtonsVisible(false)
        lifecycleScope.launch(Dispatchers.IO){
            try{
                mainViewModel.initData()
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext, "data loaded", Toast.LENGTH_SHORT).show();
                    setButtonsVisible(true)
                }

            }catch (e:Exception){
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext, "data load error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private fun setButtonsVisible(isVisible : Boolean){
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        button_add.visibility = visibility
        button_crypto.visibility = visibility
        button_rates.visibility = visibility
    }

    private fun initListView(){
        val recyclerView = findViewById<RecyclerView>(R.id.list_assets)
        val adapter = AssetListAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        assetViewModel.allAssets.observe(this, androidx.lifecycle.Observer { assets ->
            assets?.let { adapter.setAssets(it) }
        })

    }

    private fun initButtons(){
        button_add.setOnClickListener {
            val intent = Intent(this@MainActivity, NewAssetActivity::class.java)
            startActivityForResult(intent, newAssetRequestCode)
        }
        button_rates.setOnClickListener {
            val intent = Intent(this@MainActivity, RatesActivity::class.java)
            startActivity(intent)
        }
        button_crypto.setOnClickListener {
            val intent = Intent(this@MainActivity, CryptoActivity::class.java)
            startActivity(intent)
        }
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