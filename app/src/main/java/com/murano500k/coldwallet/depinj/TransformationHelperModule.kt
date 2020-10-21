package com.murano500k.coldwallet.depinj

import android.content.Context
import com.murano500k.coldwallet.repo.Repository
import com.murano500k.coldwallet.transofm.TransormationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object TransformationHelperModule {
    @Singleton
    @Provides
    fun provideTransformationHelper(@ApplicationContext context: Context, repository: Repository): TransormationHelper {
        return TransormationHelper(context, repository)
    }

}
