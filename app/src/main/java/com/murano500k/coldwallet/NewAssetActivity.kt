package com.murano500k.coldwallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.murano500k.coldwallet.database.Asset
import com.murano500k.coldwallet.net.CryptoViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.activity_new_asset.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


@AndroidEntryPoint
@ActivityScoped
class NewAssetActivity : AppCompatActivity() {

    companion object {
        const val TAG = "NewAssetActivity"
        const val EXTRA_ASSET = "com.murano500k.coldwallet.REPLY"
    }
    private var isCrypto = true
    private var currenciesList: List<String> =ArrayList()
    private lateinit var mAsset: Asset

    private lateinit var cryptoViewModel: CryptoViewModel
    private val newAssetViewModel: NewAssetViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_asset)
        setupViewModel()

        switchFiatCrypto.setOnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
            if (isCrypto != isChecked) {
                updateCurrenciesList()
                isCrypto = isChecked
            }
        }

        button_save.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(edit_amount.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                replyIntent.putExtra(EXTRA_ASSET, parseAsset())
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
        updateCurrenciesList()
        initAsset()
        initValues()
    }

    private fun setupViewModel() {
        cryptoViewModel = ViewModelProvider(this).get(CryptoViewModel::class.java)
    }


    private fun initAsset() {
        if(intent.getParcelableExtra<Asset>(EXTRA_ASSET)!=null) {
            mAsset = intent.getParcelableExtra<Asset>(EXTRA_ASSET)!!
        }else{
            mAsset = Asset(0, CURRENCY_TYPE.FIAT.ordinal, 0.0f, "USD","USD amount", "my USD amount" )
        }
    }


    private fun initValues() {
        switchFiatCrypto.isChecked = (mAsset.type == CURRENCY_TYPE.CRYPTO.ordinal)
        currenciesList?.let {
            spinnerCurrencies.setSelection(it.indexOf(mAsset.currency))
        }

        edit_name.setText(mAsset.name)
        edit_description.setText(mAsset.description)
        edit_amount.setText(mAsset.amount.toString())
    }


    private fun updateCurrenciesList() {
        lifecycleScope.launch(Dispatchers.IO){
            currenciesList = newAssetViewModel.getCryptoCodes()
            withContext(Dispatchers.Main){
                setSpinnerItems(currenciesList)
            }
        }

        /*if(isCrypto){

        } else {
            currenciesList =getFiatCurrencyCodes()
            setSpinnerItems(currenciesList)
        }*/


    }
    private fun setSpinnerItems( currenciesList: List<String>){
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currenciesList )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        edit_currency.setAdapter(adapter)
        spinnerCurrencies.adapter = adapter

        spinnerCurrencies.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val name = adapter.getItem(position)
                Log.w(TAG, "onItemSelected: $name amount" );
                edit_name.setText("$name amount")
                edit_name.selectAll()
            }

        }
        adapter.notifyDataSetChanged()
        switchFiatCrypto.isChecked = isCrypto
    }



    private fun parseAsset(): Asset {
        val type: Int
        if(switchFiatCrypto.isChecked) {
            type = CURRENCY_TYPE.FIAT.ordinal
        } else {
            type = CURRENCY_TYPE.CRYPTO.ordinal
        }
        val amount = edit_amount.text.toString().toFloat()
        val currency = spinnerCurrencies.getSelectedItem().toString()
        //val currency = edit_currency.text.toString()
        val name = edit_name.text.toString()
        val description = edit_description.text.toString()
        val id = mAsset.id

        return Asset(id, type, amount, currency, name, description)
    }


    private fun getFiatCurrencyCodes(): List<String>{
        return Currency.getAvailableCurrencies().toList()
            .map { (it.currencyCode)}.sortedBy { it }
    }
}