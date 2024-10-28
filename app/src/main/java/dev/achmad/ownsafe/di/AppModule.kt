package dev.achmad.ownsafe.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.achmad.core.preference.PreferenceStore
import dev.achmad.ownsafe.ApplicationPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationPreferences(
        preferenceStore: PreferenceStore
    ) = ApplicationPreferences(preferenceStore)

}