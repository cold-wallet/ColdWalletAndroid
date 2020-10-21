package com.nicoqueijo.android.currencyconverter.kotlin.depinj

import android.content.Context
import android.content.SharedPreferences
import com.murano500k.coldwallet.database.AssetDao
import com.murano500k.coldwallet.database.CryptoCodeDao
import com.murano500k.coldwallet.database.CryptoPricePairDao
import com.murano500k.coldwallet.database.FiatPricePairDao
import com.murano500k.coldwallet.net.api.ApiServiceBinance
import com.murano500k.coldwallet.net.api.ApiServiceMono
import com.murano500k.coldwallet.repo.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        @ApplicationContext context: Context,
        apiServiceBinance: ApiServiceBinance,
        apiServiceMono: ApiServiceMono,
        assetDao: AssetDao,
        cryptoCodeDao: CryptoCodeDao,
        cryptoPricePairDao: CryptoPricePairDao,
        fiatPricePairDao: FiatPricePairDao,
        sharedPreferences: SharedPreferences): Repository {
        return Repository(
            context,
            apiServiceBinance,
            apiServiceMono,
            assetDao,
            cryptoCodeDao,
            cryptoPricePairDao,
            fiatPricePairDao,
            sharedPreferences)
    }
}