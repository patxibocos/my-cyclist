package io.github.patxibocos.roadcyclingdata.ui.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun ddMMMFormat(localDate: LocalDate): String =
    localDate.format(DateTimeFormatter.ofPattern("dd MMM"))

fun isoDateFormat(localDate: LocalDate): String =
    localDate.format(DateTimeFormatter.ISO_DATE)
