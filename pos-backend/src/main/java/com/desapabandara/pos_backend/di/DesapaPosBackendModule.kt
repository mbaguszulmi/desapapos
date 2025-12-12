package com.desapabandara.pos_backend.di

import co.mbznetwork.android.base.eventbus.AuthEventBus
import co.mbznetwork.android.base.util.GsonSanitizedTypeAdapterFactory
import com.desapabandara.pos_backend.BuildConfig
import com.desapabandara.pos_backend.api.DesapaApi
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DesapaPosBackendModule {
    private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

    @Provides
    @Singleton
    @DesapaPosAuthInterceptor
    fun provideDesapaPosAuthInterceptor(
        authEventBus: AuthEventBus
    ): Interceptor {
        return Interceptor { chain ->
            val token = authEventBus.currentToken.value
            val response = if (token.isBlank()) {
                chain.proceed(chain.request())
            } else {
                chain.proceed(chain.request()
                    .newBuilder().header("Authorization", "Bearer $token").build())
            }

            if (response.code == 401) {
                authEventBus.requestLogout()
            }

            response
        }
    }

    @Provides
    @Singleton
    @DesapaPosHttpClient
    fun provideDesapaPosHttpClient(
        okHttpClientBuilder: OkHttpClient.Builder,
        loggingInterceptor: HttpLoggingInterceptor,
        @DesapaPosAuthInterceptor authInterceptor: Interceptor
    ): OkHttpClient =
        okHttpClientBuilder.addInterceptor(authInterceptor)
            .addNetworkInterceptor(loggingInterceptor).build()

    @Provides
    @Singleton
    @DesapaPosRetrofit
    fun provideDesapaPosRetrofit(
        @DesapaPosHttpClient httpClient: OkHttpClient
    ) = Retrofit.Builder().baseUrl(BuildConfig.POS_BASE_URL)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setDateFormat(DATE_FORMAT)
                    .registerTypeAdapterFactory(GsonSanitizedTypeAdapterFactory()).create()
            )
        )
        .client(httpClient)
        .build()

    @Provides
    @Singleton
    fun provideDesapaPosApiService(
        @DesapaPosRetrofit retrofit: Retrofit
    ) = retrofit.create(DesapaApi::class.java)
}
