package dev.achmad.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.achmad.data.api.auth.AuthDataSource
import dev.achmad.data.api.auth.AuthService
import dev.achmad.data.api.stats.StatsDataSource
import dev.achmad.data.api.stats.StatsService
import dev.achmad.data.api.user.UserDataSource
import dev.achmad.data.api.user.UserService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideAuthDataSource(
        authService: AuthService
    ) = AuthDataSource(authService)

    @Provides
    @Singleton
    fun provideUserDataSource(
        userService: UserService
    ): UserDataSource = UserDataSource(userService)

    @Provides
    @Singleton
    fun provideStatsDataSource(
        statsService: StatsService
    ): StatsDataSource = StatsDataSource(statsService)

}