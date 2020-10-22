package com.murano500k.coldwallet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.murano500k.coldwallet.CURRENCY_TYPE
import com.murano500k.coldwallet.R
import com.murano500k.coldwallet.database.Asset

class NewAssetFragment : Fragment() {

    private var isCrypto = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var asset = arguments?.getParcelable<Asset>("arg_asset")
        if(asset!=null){
            isCrypto = (asset.type == CURRENCY_TYPE.CRYPTO.ordinal)
        }else{
            isCrypto = arguments?.getBoolean("arg_is_crypto")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_asset, container, false)
    }


}