package com.murano500k.coldwallet.model

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.murano500k.coldwallet.CURRENCY_TYPE
import com.murano500k.coldwallet.R
import com.murano500k.coldwallet.TAG
import com.murano500k.coldwallet.database.Asset
import com.murano500k.coldwallet.viewmodel.AssetListViewModel


class AssetListAdapter internal constructor(
    private val context: Context,
    private val listener: OnEditListener,
    private val viewModel: AssetListViewModel
) : RecyclerView.Adapter<AssetListAdapter.AssetViewHolder>() {
companion object{
    const val TAG = "AssetListAdapter"
}
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var assets = emptyList<Asset>() // Cached copy of assets

    inner class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCurrency: TextView = itemView.findViewById(R.id.textCurrency)
        val textName: TextView = itemView.findViewById(R.id.textName)
        val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        var textAmount: EditText = itemView.findViewById(R.id.textAmount)
        var buttonRemove: ImageButton = itemView.findViewById(R.id.buttonRemove)
        var imageType: ImageView = itemView.findViewById(R.id.imageCurrencyType)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val itemView = inflater.inflate(R.layout.asset_list_item, parent, false)
        return AssetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val current = assets[position]
        holder.textCurrency.text = current.currency
        holder.textName.text = current.name
        holder.textDescription.text = current.description
        holder.textDescription.visibility = GONE

        holder.textAmount.setText(current.amount.toString())
        holder.textAmount.onFocusChangeListener = View.OnFocusChangeListener(function = {
                view, hasFocus ->
            if(!hasFocus) {
                var newAmount = holder.textAmount.text.toString()
                if(newAmount == current.amount.toString()) return@OnFocusChangeListener
                if (newAmount.isNotBlank()) {
                    if (!newAmount.contains(".")) newAmount += ".0"
                    try {
                        val newAmountNum = newAmount.toFloat()
                        current.amount = newAmountNum
                        Log.w(TAG, "2 viewModel.updateAsset(${current.amount})")
                        viewModel.updateAsset(current)
                        return@OnFocusChangeListener
                    } catch (e: NumberFormatException) {

                    }
                }else {
                    holder.textAmount.setText(current.amount.toString())
                }
            }
        })

        if(current.type == CURRENCY_TYPE.CRYPTO.ordinal) {
            holder.imageType.setImageResource(R.drawable.ic_crypto)
        }else {
            holder.imageType.setImageResource(R.drawable.ic_dollar)
        }
        holder.buttonRemove.setOnClickListener {
            listener.deleteButtonClicked(current)
        }
    }

    internal fun setAssets(assets: List<Asset>) {
        this.assets = assets
        notifyDataSetChanged()
    }

    override fun getItemCount() = assets.size

    interface OnEditListener {
        fun deleteButtonClicked(asset: Asset)
        fun onEditClicked(asset: Asset)

    }
}