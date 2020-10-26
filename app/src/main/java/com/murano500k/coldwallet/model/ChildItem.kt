package com.murano500k.coldwallet.model

import androidx.core.content.ContextCompat.getColor
import com.murano500k.coldwallet.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_stats_child.view.*

open class ChildItem(val title: String, val isCrypto: Boolean) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.root.child_textTitle.text = title
        if(isCrypto){
            viewHolder.root.setBackgroundColor(getColor(viewHolder.root.context,
                R.color.colorBgCrypto
            ))
        }else{
            viewHolder.root.setBackgroundColor(getColor(viewHolder.root.context,
                R.color.colorBgFiat
            ))
        }

    }
    override fun getLayout(): Int {
        return R.layout.item_stats_child
    }

}