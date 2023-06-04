package com.raxim.myscoutee.profile

import com.raxim.myscoutee.profile.data.document.mongo.EventItem
import com.raxim.myscoutee.profile.data.document.mongo.RangeLocal
import org.bson.BsonBinary
import org.bson.BsonBinarySubType
import org.bson.UuidRepresentation
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ThreadLocalRandom

fun String.asUUID(): String {
    val bytes = Base64.getDecoder().decode(this)
    return BsonBinary(BsonBinarySubType.UUID_LEGACY, bytes).asUuid(UuidRepresentation.JAVA_LEGACY).toString()
}

fun UUID.asBase64(): String {
    return Base64.getEncoder().encodeToString(BsonBinary(this, UuidRepresentation.JAVA_LEGACY).data)
}

class Base64Test {

    @Disabled
    @Test
    fun base64() {
        println(UUID.fromString("ce0e1cec-f698-4888-aedf-b1dc9aa2596f").asBase64())
        println("30/Awa250nrw1WKr37makg==".asUUID())
    }

    @Test
    fun hash() {
        var result = Date().hashCode()
        result = 31 * result + true.hashCode()
        println(result)
    }

    @Disabled
    @Test
    fun daysDiff() {
        val start = LocalDateTime.of(1998, 12, 1, 0, 0)
        val end = LocalDateTime.of(1997, 12, 1, 0, 0)

        val daysBetween = ChronoUnit.DAYS.between(
            start, end
        ).toInt()

        println(daysBetween)
    }

    @Disabled
    @Test
    fun maxDate() {
        val items = mutableListOf<EventItem>()
        for (i in 0..5) {
            items.add(genEventItem())
        }

        println(items)

        val maxRange = items.map { item -> item.range }.reduce { acc, range ->
            var start = acc!!.start
            if (range!!.start!!.isBefore(start)) {
                start = range.start!!
            }

            var end = acc.end
            if (range.end!!.isAfter(end)) {
                end = range.end!!
            }
            RangeLocal(start, end)
        }

        println(maxRange)
    }

    private fun genEventItem(): EventItem {
        val startDay = LocalDateTime.of(1970, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC)
        val maxDay = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

        val startRand: Long = ThreadLocalRandom.current().nextLong(startDay, maxDay)
        val start = LocalDateTime.ofEpochSecond(startRand, 0, ZoneOffset.UTC)

        val midDay = start.toEpochSecond(ZoneOffset.UTC)

        val endRand: Long = ThreadLocalRandom.current().nextLong(midDay, maxDay)
        val end = LocalDateTime.ofEpochSecond(endRand, 0, ZoneOffset.UTC)

        val range = RangeLocal(start, end)
        return EventItem(range = range)
    }

    @Disabled
    @Test
    fun monthsDiff() {
        val profile1YM = YearMonth.from(
            Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        )

        val profile2YM = YearMonth.from(
            LocalDate.parse("2025-01-01")
        )

        val monthsBetween = ChronoUnit.MONTHS.between(
            profile1YM, profile2YM
        ).toDouble()

        println(monthsBetween)
    }
}