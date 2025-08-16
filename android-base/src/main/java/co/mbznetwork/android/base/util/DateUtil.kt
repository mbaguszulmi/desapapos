package co.mbznetwork.android.base.util

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

object DateUtil {
    @SuppressLint("SimpleDateFormat")
    fun format(date: Date, format: String): String {
        val formatter = SimpleDateFormat(format)
        return formatter.format(date)
    }

    fun formatDateTimeShort(date: Date): String {
        val formatter = SimpleDateFormat.getDateTimeInstance(
            DateFormat.SHORT,
            DateFormat.SHORT
        )
        return formatter.format(date)
    }
}