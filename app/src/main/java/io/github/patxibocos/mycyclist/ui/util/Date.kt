package io.github.patxibocos.mycyclist.ui.util

import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun ddMMMFormat(localDate: LocalDate): String =
    localDate.format(DateTimeFormatter.ofPattern("dd MMM"))

fun isoFormat(zonedDateTime: ZonedDateTime): String =
    if (hourIsMissing(zonedDateTime)) {
        zonedDateTime.format(DateTimeFormatter.ofPattern("d MMMM"))
    } else {
        zonedDateTime.format(DateTimeFormatter.ofPattern("d MMMM - HH:mm"))
    }

fun hourIsMissing(zonedDateTime: ZonedDateTime): Boolean =
    with(zonedDateTime.withZoneSameInstant(ZoneOffset.UTC)) {
        hour == 0 && minute == 0 && second == 0
    }
