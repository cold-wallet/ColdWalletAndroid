package com.nicoqueijo.android.currencyconverter.kotlin.depinj

import android.content.Context
import com.murano500k.coldwallet.database.AssetDao
import com.murano500k.coldwallet.database.CryptoCodeDao
import com.murano500k.coldwallet.database.CryptoPricePairDao
import com.murano500k.coldwallet.database.MyRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideCurrencyDatabase(@ApplicationContext context: Context): MyRoomDatabase {
        return MyRoomDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideAssetDao(database: MyRoomDatabase): AssetDao {
        return database.assetDao()
    }


    @Singleton
    @Provides
    fun provideCryptoCodeDao(database: MyRoomDatabase): CryptoCodeDao {
        return database.cryptoCodeDao()
    }

    @Singleton
    @Provides
    fun provideCryptoPricePairDao(database: MyRoomDatabase): CryptoPricePairDao {
        return database.cryptoPricePairDao()
    }
}