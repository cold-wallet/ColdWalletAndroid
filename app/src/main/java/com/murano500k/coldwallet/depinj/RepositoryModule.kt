package com.nicoqueijo.android.currencyconverter.kotlin.depinj

import android.content.Context
import android.content.SharedPreferences
import com.murano500k.coldwallet.database.AssetDao
import com.murano500k.coldwallet.database.CryptoCodeDao
import com.murano500k.coldwallet.database.CryptoPricePairDao
import com.murano500k.coldwallet.net.api.ApiService
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
            apiService: ApiService,
            assetDao: AssetDao,
            cryptoCodeDao: CryptoCodeDao,
            cryptoPricePairDao: CryptoPricePairDao,
            sharedPreferences: SharedPreferences): Repository {
        return Repository(context, apiService, assetDao, cryptoCodeDao, cryptoPricePairDao, sharedPreferences)
    }
}