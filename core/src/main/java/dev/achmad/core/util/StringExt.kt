package dev.achmad.core.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun ZonedDateTimeString.toZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}