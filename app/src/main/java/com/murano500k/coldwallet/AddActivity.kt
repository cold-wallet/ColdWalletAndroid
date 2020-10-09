package com.murano500k.coldwallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.murano500k.coldwallet.Constants.EXTRA_CURRENCY
import com.murano500k.coldwallet.assets.SimpleFiatAdapter
import kotlinx.android.synthetic.main.activity_add.*
import java.util.*

class AddActivity : Activity(), SimpleFiatAdapter.ItemClickListerner {

    private lateinit var adapter: SimpleFiatAdapter

    companion object {
        const val TAG = "AddActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        initAdapter()
    }

    private fun initAdapter() {
        val currencies= Currency.getAvailableCurrencies().toList()
        Log.w(TAG, "log ${currencies.size} " );
        adapter = SimpleFiatAdapter(currencies, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

    }

    override fun itemClicked(currency: Currency) {
        val intent = Intent()
        intent.putExtra(EXTRA_CURRENCY, currency);
        setResult(RESULT_OK, intent)
        finish()
    }

}