package com.murano500k.coldwallet.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.murano500k.coldwallet.AssetListAdapter
import com.murano500k.coldwallet.CURRENCY_TYPE
import com.murano500k.coldwallet.R
import com.murano500k.coldwallet.database.Asset
import com.murano500k.coldwallet.viewmodel.AssetListViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.android.synthetic.main.fragment_asset_list.*

@AndroidEntryPoint
@FragmentScoped
class AssetListFragment : Fragment(), AssetListAdapter.OnEditListener {

    companion object{
        const val TAG = "AssetListFragment"
    }

    private val viewModel: AssetListViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_asset_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initButtons()
    }

    private fun initButtons() {
        buttonAddAsset.setOnClickListener{
            showSelectAssetTypeDialog()
        }
    }

    private fun initList() {
        val adapter = context?.let { AssetListAdapter(it, this) }
        recyclerViewAssets.adapter = adapter
        recyclerViewAssets.layoutManager = LinearLayoutManager(context)
        viewModel.allAssets.observe(viewLifecycleOwner, androidx.lifecycle.Observer { assets ->
            assets?.let { adapter!!.setAssets(it) }
        })
        recyclerViewAssets.layoutManager = LinearLayoutManager(context)
    }

    override fun deleteButtonClicked(asset: Asset) {
        showDeleteDialog(asset)
    }

    override fun onEditClicked(asset: Asset) {
        var bundle = bundleOf(
            "arg_asset" to asset,
            "arg_is_crypto" to (asset.type == CURRENCY_TYPE.CRYPTO.ordinal)
        )
        findNavController().navigate(R.id.action_assetListFragment_to_newAssetFragment, bundle)
    }

    fun showDeleteDialog(asset: Asset){
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to delete ${asset.name}?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                viewModel.delete(asset)
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    fun showSelectAssetTypeDialog(){
        Log.w(TAG, "showSelectAssetTypeDialog: " );
        val builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.select_asset_type)
            .setCancelable(true)
            .setPositiveButton(R.string.fiat) { dialog, id ->
                navigateToNewAsset(false)
            }
            .setNegativeButton(R.string.crypto) { dialog, id ->
                // Dismiss the dialog
                navigateToNewAsset(true)
            }
        val alert = builder.create()
        alert.show()
    }

    private fun navigateToNewAsset(isCrypto: Boolean ) {
        var bundle = bundleOf(
            "arg_is_crypto" to isCrypto
        )
        findNavController().navigate(R.id.action_assetListFragment_to_newAssetFragment, bundle)
    }


}