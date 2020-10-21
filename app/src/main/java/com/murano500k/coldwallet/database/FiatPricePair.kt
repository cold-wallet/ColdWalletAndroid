package com.murano500k.coldwallet.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "fiatpricepair_table")
data class FiatPricePair(@PrimaryKey @ColumnInfo(name = "currencyCodeA") val currencyCodeA: String,
                         @ColumnInfo(name = "currencyCodeB") val currencyCodeB: String,
                         @ColumnInfo(name = "rateCross") val rateCross: String

) : Parcelable
