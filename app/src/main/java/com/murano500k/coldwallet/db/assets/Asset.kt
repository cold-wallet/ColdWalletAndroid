package com.murano500k.coldwallet.db.assets

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "asset_table")
data class Asset(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int,
                 @ColumnInfo(name = "type") val type: Int,
                 @ColumnInfo(name = "amount") var amount : Float,
                 @ColumnInfo(name = "currency") val currency: String,
                 @ColumnInfo(name = "name") val name: String,
                 @ColumnInfo(name = "description") val description: String
) : Parcelable
