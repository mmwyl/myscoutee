package com.raxim.myscoutee.profile.controller

import com.raxim.myscoutee.profile.data.document.mongo.RangeLocal
import java.net.URLDecoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.*

val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

const val dateRegex =
    "((?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[\\s\\S]*(?:(?:1|2)[0-9]{3}))[\\s\\S]?[\u2010-\u2015\\-]?[\\s\\S]?((?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)?[\\s\\S]*(?:(?:1|2)[0-9]{3}))?[\\s\\S]?"

val formatter: DateTimeFormatter = DateTimeFormatterBuilder()
    .appendPattern("MMM yyyy")
    .toFormatter()

fun String.range(): RangeLocal {
    val dateParts = "[\u2010-\u2015\\-]".toRegex().split(this)

    val start = formatter.parse(dateParts[0].trim())

    val startDate = LocalDateTime.of(start.get(ChronoField.YEAR), start.get(ChronoField.MONTH_OF_YEAR), 1, 0, 0)
        .truncatedTo(ChronoUnit.DAYS)

    var endDate: LocalDateTime? = null
    if (dateParts[1].isNotBlank()) {
        val end = formatter.parse(dateParts[1].trim())

        var endL = LocalDate.of(end.get(ChronoField.YEAR), end.get(ChronoField.MONTH_OF_YEAR), 1)
        endL = endL.withDayOfMonth(endL.lengthOfMonth())

        endDate = endL.atStartOfDay().truncatedTo(ChronoUnit.DAYS)
    }

    return RangeLocal(startDate, endDate)
}

fun String.decode(): String {
    return URLDecoder.decode(this, "UTF-8")
}

fun String.uuid(): UUID? {
    return if (this != null) {
        UUID.fromString(this)
    } else {
        null;
    }
}