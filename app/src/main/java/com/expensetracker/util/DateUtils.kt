package com.expensetracker.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    fun formatDisplayDate(millis: Long): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun formatShortDate(millis: Long): String {
        val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun formatIsoDate(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return formatter.format(Date(millis))
    }

    fun getTodayStartOfDay(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getTodayEndOfDay(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    fun getFirstDayOfCurrentMonth(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * Converts Material 3 DatePicker UTC midnight millis to local midnight millis.
     */
    fun utcMidnightToLocalMidnight(utcMillis: Long): Long {
        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = utcMillis
        }
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, utcCal.get(Calendar.YEAR))
            set(Calendar.MONTH, utcCal.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * Converts local midnight millis to Material 3 DatePicker UTC midnight millis.
     */
    fun localMidnightToUtcMidnight(localMillis: Long): Long {
        val localCal = Calendar.getInstance().apply {
            timeInMillis = localMillis
        }
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(Calendar.YEAR, localCal.get(Calendar.YEAR))
            set(Calendar.MONTH, localCal.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, localCal.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis
    }

    /**
     * Normalizes a timestamp to the very end of that day (23:59:59.999).
     */
    fun toEndOfDay(millis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    /**
     * Normalizes a timestamp to the start of that day (00:00:00.000).
     */
    fun toStartOfDay(millis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}

