package com.murano500k.coldwallet.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.murano500k.coldwallet.CURRENCY_TYPE
import com.murano500k.coldwallet.R
import com.murano500k.coldwallet.activity.NewAssetActivity
import com.murano500k.coldwallet.database.Asset
import com.murano500k.coldwallet.viewmodel.NewAssetViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.activity_new_asset.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
@ActivityScoped
class NewAssetFragment : Fragment() {

    private val newAssetViewModel: NewAssetViewModel by viewModels()

    private var isCrypto = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCrypto = arguments?.getBoolean("arg_is_crypto")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_asset, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSaveButton()
        updateCurrenciesList()

    }

    private fun initSaveButton() {
        button_save.setOnClickListener {
            if (TextUtils.isEmpty(edit_amount.text)) {
                Snackbar.make(requireView(),"Error", Snackbar.LENGTH_SHORT)
            } else {
                lifecycleScope.launch(Dispatchers.IO){
                    newAssetViewModel.insertAsset(parseAsset())
                }
                findNavController().navigate(R.id.action_newAssetFragment_to_assetListFragment)
            }
        }
    }


    private fun updateCurrenciesList() {
        if(isCrypto){
            lifecycleScope.launch(Dispatchers.IO){
                val currenciesList = newAssetViewModel.getCryptoCodes()
                withContext(Dispatchers.Main){
                    setSpinnerItems(currenciesList)
                }
            }
        } else {
            val currenciesList =newAssetViewModel.getFiatCodes()
            setSpinnerItems(currenciesList)
        }
    }

    private fun setSpinnerItems( currenciesList: List<String>){
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currenciesList )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrencies.adapter = adapter

        spinnerCurrencies.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val name = adapter.getItem(position)
                Log.w(NewAssetActivity.TAG, "onItemSelected: $name amount" );
                edit_name.setText("$name amount")
                edit_name.selectAll()
            }

        }
        adapter.notifyDataSetChanged()
    }

    private fun parseAsset(): Asset {
        val type: Int
        if(isCrypto) {
            type = CURRENCY_TYPE.CRYPTO.ordinal
        } else {
            type = CURRENCY_TYPE.FIAT.ordinal
        }
        val amount = edit_amount.text.toString().toFloat()
        val currency = spinnerCurrencies.getSelectedItem().toString()
        //val currency = edit_currency.text.toString()
        val name = edit_name.text.toString()
        val description = edit_description.text.toString()

        return Asset(0, type, amount, currency, name, description)
    }


}