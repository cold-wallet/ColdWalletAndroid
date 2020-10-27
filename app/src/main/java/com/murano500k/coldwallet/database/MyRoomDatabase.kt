package com.murano500k.coldwallet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(
    Asset::class,
    CryptoCode::class,
    CryptoPricePair::class,
    FiatPricePair::class
), version = 6, exportSchema = false)
abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun assetDao(): AssetDao

    abstract fun cryptoCodeDao(): CryptoCodeDao

    abstract fun cryptoPricePairDao(): CryptoPricePairDao

    abstract fun fiatPricePairDao(): FiatPricePairDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: MyRoomDatabase? = null

        fun getDatabase(context: Context): MyRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyRoomDatabase::class.java,
                    "word_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}