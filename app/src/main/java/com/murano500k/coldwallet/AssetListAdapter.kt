package com.murano500k.coldwallet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.murano500k.coldwallet.assets.Asset


class AssetListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<AssetListAdapter.AssetViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var assets = emptyList<Asset>() // Cached copy of assets

    inner class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCurrency: TextView = itemView.findViewById(R.id.textCurrency)
        val textName: TextView = itemView.findViewById(R.id.textName)
        val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        var editTextAmount: EditText = itemView.findViewById(R.id.editTextAmount)

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
        holder.editTextAmount.setText(current.amount.toString())
    }

    internal fun setAssets(assets: List<Asset>) {
        this.assets = assets
        notifyDataSetChanged()
    }

    override fun getItemCount() = assets.size
}