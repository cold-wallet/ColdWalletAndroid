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
        const val EXTRA_REPLY = "com.murano500k.coldwallet.REPLY"
    }
    private var isCrypto = false

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
                val asset = parseAsset()

                replyIntent.putExtra(EXTRA_REPLY, asset)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
        updateCurrenciesList()
        initSwtich()
    }

    private fun initSwtich() {

    }

    private fun updateCurrenciesList() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        val currenciesList: List<String>

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

        return Asset(0, type, amount, currency, name, description)
    }


    private fun getFiatCurrencyCodes(): List<String>{
        return Currency.getAvailableCurrencies().toList()
            .map { (it.currencyCode)}.sortedBy { it }
    }
}