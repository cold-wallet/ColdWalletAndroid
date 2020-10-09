package com.murano500k.coldwallet.assets

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.murano500k.coldwallet.R
import com.murano500k.coldwallet.inflate
import java.util.*

class SimpleFiatAdapter(private var items: List<Currency>, private var listener: ItemClickListerner) : RecyclerView.Adapter<SimpleFiatAdapter.CryptoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        val inflatedView = parent.inflate(R.layout.simple_list_item, false)
        return CryptoViewHolder(inflatedView)    }

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        val item = items[position]
        val text = "${item.currencyCode} ${item.displayName}"
        holder.textView.text = text
        holder.view.setOnClickListener { listener.itemClicked(item) }
    }

    override fun getItemCount(): Int {
        return items.size
    }
    class CryptoViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        var view: View = v
        var textView: TextView = v.findViewById(R.id.textView)
        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }

    }

    interface ItemClickListerner{
        fun itemClicked(currency: Currency)
    }
}