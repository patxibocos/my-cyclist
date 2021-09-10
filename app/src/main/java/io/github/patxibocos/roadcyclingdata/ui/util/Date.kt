package io.github.patxibocos.roadcyclingdata.ui.util

import com.google.protobuf.Timestamp
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun ddMMMFormat(timestamp: Timestamp): String =
    Instant.ofEpochSecond(timestamp.seconds).atZone(ZoneOffset.UTC).toLocalDate().format(
        DateTimeFormatter.ofPattern("dd MMM")
    )

fun isoDateFormat(timestamp: Timestamp): String =
    Instant.ofEpochSecond(timestamp.seconds).atZone(ZoneOffset.UTC).toLocalDate()
        .format(DateTimeFormatter.ISO_DATE)
