package dev.achmad.core.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.achmad.core.DEFAULT_HOST
import dev.achmad.core.preference.PreferenceStore
import dev.achmad.core.network.NetworkPreferences
import dev.achmad.core.network.interceptor.CookieInterceptor
import dev.achmad.core.network.interceptor.HostSelectionInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkPreferences(
        preferenceStore: PreferenceStore
    ) = NetworkPreferences(preferenceStore)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        networkPreferences: NetworkPreferences
    ): OkHttpClient {
        val hostSelectionInterceptor = HostSelectionInterceptor {
            networkPreferences.host().get()
        }
        val cookieInterceptor = CookieInterceptor(networkPreferences)
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .addInterceptor(hostSelectionInterceptor)
            .addInterceptor(cookieInterceptor)
            .addInterceptor(ChuckerInterceptor(context))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://$DEFAULT_HOST/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
    }

}