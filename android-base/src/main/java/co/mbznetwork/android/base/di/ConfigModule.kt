package co.mbznetwork.android.base.di

import android.content.Context
import android.provider.Settings
import co.mbznetwork.android.base.model.DeviceType
import co.mbznetwork.android.base.storage.AppDataStore
import co.mbznetwork.android.base.util.GsonSanitizedTypeAdapterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {
    @Provides
    @Singleton
    fun providesDataStore(@ApplicationContext context: Context, gson: Gson): AppDataStore = AppDataStore(context, gson)

    @Provides
    @Singleton
    fun providesGson(): Gson = GsonBuilder().registerTypeAdapterFactory(
        GsonSanitizedTypeAdapterFactory()
    ).create()

    @Provides
    @Singleton
    fun provideDefaultDispatcher(): DispatcherProvider = DefaultDispatcherProvider()

    @DefaultDispatcher
    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    @DeviceID
    fun provideDeviceID(@ApplicationContext context: Context): String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    @Provides
    @Singleton
    fun provideDeviceType(@ApplicationContext context: Context): DeviceType {
        val metrics = context.resources.displayMetrics
        val widthInches = metrics.widthPixels / metrics.xdpi
        val heightInches = metrics.heightPixels / metrics.ydpi
        val diagonalInches = Math.sqrt((widthInches * widthInches + heightInches * heightInches).toDouble())
        return if (diagonalInches >= 7.0) DeviceType.Tablet else DeviceType.Phone
    }

}