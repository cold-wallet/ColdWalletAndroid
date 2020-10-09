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


    companion object {
        const val REQUEST_ADD_CURRENCY = 13
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)





        button_add.setOnClickListener {
            val intent = Intent(this@MainActivity, NewAssetActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }
        assetViewModel = ViewModelProvider(this).get(AssetViewModel::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.list_assets)
        val adapter = AssetListAdapter(this, assetViewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        assetViewModel.allAssets.observe(this, androidx.lifecycle.Observer { assets ->
            assets?.let { adapter.setAssets(it) }
        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<Asset>(NewAssetActivity.EXTRA_REPLY)?.let {
                assetViewModel.insert(it)
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