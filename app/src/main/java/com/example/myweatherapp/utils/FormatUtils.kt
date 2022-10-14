package com.example.myweatherapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.myweatherapp.R
import java.time.Instant
import java.util.*
import kotlin.math.roundToInt

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle


@RequiresApi(Build.VERSION_CODES.O)
fun getFormattedDate(date: Int): String {
    val dateTime = getDateTimeFromEpoch(date)
    return formatDateParts(getDateFromDateTime(dateTime))
}

fun getFormattedTime(date: Int): String {
    val dateTime = getDateTimeFromEpoch(date)
    return getTimeFromDateTime(dateTime)
}


fun getFormattedUVRange(uv: Double): String {
    return when {
        uv <= 2 -> "Low"
        uv > 2 && uv <= 5 -> "Moderate"
        uv > 5 && uv <= 7 -> "High"
        uv > 7 && uv <= 11 -> "Very High"
        else -> "Extreme"
    }
}

fun getHumidityInPercent(humidity: Int): String {
    return "${humidity}%"
}

fun getImageFromUrl(url: String): Int {
    return when (url) {
        "01d" -> R.drawable.icn_sun
        "01n" -> R.drawable.icn_moon
        "02d" -> R.drawable.icn_few_clouds
        "02n" -> R.drawable.icn_night_clouds
        "03d", "03n" -> R.drawable.icn_scattered_clouds
        "04d", "04n" -> R.drawable.icn_broken_clouds
        "09d", "09n" -> R.drawable.icn_shower_rain
        "10d", "10n" -> R.drawable.icn_rain
        "11d", "11n" -> R.drawable.icn_thunderstorm
        "13d", "13n" -> R.drawable.icn_snow
        "50d", "50n" -> R.drawable.icn_mist
        else -> R.drawable.ic_launcher_foreground
    }
}

fun getDateTimeFromEpoch(date: Int): String {
    return kotlinx.datetime.Instant
        .fromEpochSeconds(date.toLong())
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .toString()
}

fun getTimeFromDateTime(dateTime: String): String {
    var i = 0
    while (dateTime[i] != 'T') i++
    return dateTime.substring(i + 1, dateTime.length)
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateParts(date: String): String {
    val datePartsArray = date.split('-')
    val formattedDateNumber = datePartsArray[2]
    val formattedMonth = mapMonthNumberToName(datePartsArray[1])
    val formattedDay = getWeekDayName(date)
    return "${formattedDay}, $formattedDateNumber $formattedMonth"
}

fun getDateFromDateTime(dateTime: String): String {
    var i = 0
    var date = ""
    while (dateTime[i] != 'T') {
        date += dateTime[i]
        i++
    }
    return date
}

fun mapMonthNumberToName(monthNumber: String): String {
    return when (monthNumber) {
        "01" -> "January"
        "02" -> "February"
        "03" -> "March"
        "04" -> "April"
        "05" -> "May"
        "06" -> "June"
        "07" -> "July"
        "08" -> "August"
        "09" -> "September"
        "10" -> "October"
        "11" -> "November"
        "12" -> "December"
        else -> "NA"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getWeekDayName(date: String): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("u-M-d", Locale.ENGLISH)
    return LocalDate.parse(date, formatter).dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
}


