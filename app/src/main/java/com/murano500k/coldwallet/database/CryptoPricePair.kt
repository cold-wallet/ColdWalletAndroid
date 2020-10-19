package com.murano500k.coldwallet.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "cryptopricepair_table")
data class CryptoPricePair(@PrimaryKey @ColumnInfo(name = "symbol") val symbol: String,
                           @ColumnInfo(name = "price") val price: String
) : Parcelable
