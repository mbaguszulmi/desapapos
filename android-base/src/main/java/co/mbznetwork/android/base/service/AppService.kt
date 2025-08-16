package co.mbznetwork.android.base.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class AppService : Service(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()

    abstract fun channelId(): String
    abstract fun notificationId(): Int
    abstract fun serviceName(): String

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = channelId()
            Timber.d("Starting as foreground service: $channel")
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                NotificationChannel(
                    channel,
                    serviceName(),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                startForeground(
                    notificationId(),
                    NotificationCompat.Builder(this, channel).build()
                )
            } else {
                startForeground(
                    notificationId(),
                    NotificationCompat.Builder(this, channel).build(), FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            }

        }
    }

    override fun onDestroy() {
        coroutineContext.cancel()
        super.onDestroy()
        Timber.d("Service destroyed")
    }
}