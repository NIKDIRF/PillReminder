package com.app.pills.model

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class Util {

    fun longToLocalDateTime(longValue: Long): LocalDateTime {
        return LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(longValue),
            ZoneId.systemDefault()
        )
    }

    fun localDateTimeToLong(localDateTime: LocalDateTime): Long {
        return localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun localTimeToLong(time: LocalTime): Long {
        return time.toSecondOfDay().toLong()
    }

    fun longToLocalTime(seconds: Long): LocalTime {
        return LocalTime.ofSecondOfDay(seconds)
    }
}