package co.mbznetwork.android.base.di

import android.content.Context
import android.net.ConnectivityManager
import co.mbznetwork.android.base.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHTTPLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        )
        return interceptor
    }

    @Provides
    @Singleton
    fun provideGenericOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(loggingInterceptor)
            .readTimeout(BuildConfig.HTTP_READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(BuildConfig.HTTP_CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.HTTP_WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGenericOkHttpClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .readTimeout(BuildConfig.HTTP_READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(BuildConfig.HTTP_CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.HTTP_WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context) = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}