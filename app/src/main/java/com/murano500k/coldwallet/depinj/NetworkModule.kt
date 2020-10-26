package com.murano500k.coldwallet.depinj

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.murano500k.coldwallet.net.api.ApiServiceBinance
import com.murano500k.coldwallet.net.api.ApiServiceMono
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideBinanceApiService(@Named("retrofitBinance") retrofit: Retrofit): ApiServiceBinance {
        return retrofit.create(ApiServiceBinance::class.java)
    }

    @Singleton
    @Provides
    fun provideMonoApiService(@Named("retrofitMono") retrofitMono: Retrofit): ApiServiceMono {
        return retrofitMono.create(ApiServiceMono::class.java)
    }

    @Singleton
    @Provides
    @Named("retrofitBinance")
    fun provideRetrofit(@Named("baseUrlBinance") baseUrl : String,
                        gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(gsonConverterFactory)
            .build()
    }


    @Singleton
    @Provides
    @Named("retrofitMono")
    fun provideRetrofitMono(@Named("baseUrlMono") baseUrlMono : String,
                            gsonConverterFactory: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrlMono)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    @Named("baseUrlBinance")
    fun provideBaseUrl(): String {
        return ApiServiceBinance.BASE_URL
    }

    @Singleton
    @Provides
    @Named("baseUrlMono")
    fun provideBaseUrlMono(): String {
        return ApiServiceMono.BASE_URL_MONO
    }
}