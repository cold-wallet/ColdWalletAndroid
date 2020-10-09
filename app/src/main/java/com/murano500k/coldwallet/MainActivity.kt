package com.murano500k.coldwallet

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.murano500k.coldwallet.Constants.EXTRA_CURRENCY
import com.murano500k.coldwallet.assets.Asset
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var assetViewModel: AssetViewModel
    private val newWordActivityRequestCode = 1


    companion object{
        const val REQUEST_ADD_CURRENCY = 13
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.list_assets)
        val adapter = AssetListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        assetViewModel = ViewModelProvider(this).get(AssetViewModel::class.java)

        assetViewModel.allAssets.observe(this, androidx.lifecycle.Observer {assets ->
            assets?.let { adapter.setAssets(it) }
        })

        button_add.setOnClickListener{
            //startActivityForResult(Intent(this, AddActivity::class.java ), REQUEST_ADD_CURRENCY)
            val intent = Intent(this@MainActivity, NewAssetActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<Asset>(NewAssetActivity.EXTRA_REPLY)?.let{
                assetViewModel.insert(it)
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG).show()
        }
        if(requestCode == REQUEST_ADD_CURRENCY){
            if(resultCode == RESULT_OK){
                val currency = data?.getSerializableExtra(EXTRA_CURRENCY) as Currency
                addNewAsset(currency)
            }
        }
    }

    private fun addNewAsset(currency: Currency) {

        Toast.makeText(this, Html.fromHtml("<font color='#e3f2fd' ><b>" + currency.displayName + "</b></font>"), Toast.LENGTH_SHORT).show();
    }
}