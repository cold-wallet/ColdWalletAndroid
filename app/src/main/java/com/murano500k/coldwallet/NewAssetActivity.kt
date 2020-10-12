package com.murano500k.coldwallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.murano500k.coldwallet.assets.Asset
import kotlinx.android.synthetic.main.activity_new_asset.*
import java.util.*


class NewAssetActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ASSET = "com.murano500k.coldwallet.REPLY"
    }
    private var isCrypto = false
    private lateinit var currenciesList: List<String>
    private lateinit var mAsset: Asset

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_asset)


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

    private fun initAsset() {
        if(intent.getParcelableExtra<Asset>(EXTRA_ASSET)!=null) {
            mAsset = intent.getParcelableExtra<Asset>(EXTRA_ASSET)!!
        }else{
            mAsset = Asset(0, CURRENCY_TYPE.FIAT.ordinal, 0.0f, "USD","USD amount", "my USD amount" )
        }
    }


    private fun initValues() {
        mAsset
        switchFiatCrypto.isChecked = (mAsset.type == CURRENCY_TYPE.CRYPTO.ordinal)
        spinnerCurrencies.setSelection(currenciesList.indexOf(mAsset.currency))
        edit_name.setText(mAsset.name)
        edit_description.setText(mAsset.description)
        edit_amount.setText(mAsset.amount.toString())
    }


    private fun updateCurrenciesList() {
        // Create an ArrayAdapter using the string array and a default spinner layout


        if(isCrypto){
            currenciesList = getFiatCurrencyCodes()
        } else {
            currenciesList = getFiatCurrencyCodes()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currenciesList )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        edit_currency.setAdapter(adapter)
        spinnerCurrencies.adapter = adapter
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
    private fun getFiatCurrencyCodes2(): List<String>{
        return Currency.getAvailableCurrencies().toList()
            .map { (it.currencyCode)}.sortedBy { it }
    }
}