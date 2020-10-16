package com.murano500k.coldwallet.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "cryptocode_table")
data class CryptoCode(@PrimaryKey @ColumnInfo(name = "name") val name: String) : Parcelable