package org.backstage.util

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

const val PATTERN_TIME = "HH:mm:ss"
const val PATTERN_DATE = "yyyy-MM-dd"
const val PATTERN_DATETIME = "$PATTERN_DATE $PATTERN_TIME"

data class TimeBand(
    @JsonFormat(pattern = PATTERN_TIME)
    val start: LocalTime,

    @JsonFormat(pattern = PATTERN_TIME)
    val end: LocalTime
)

data class DateBand(
    @JsonFormat(pattern = PATTERN_DATE)
    val start: LocalDate,

    @JsonFormat(pattern = PATTERN_DATE)
    val end: LocalDate
)

data class DateTimeBand(
    @JsonFormat(pattern = PATTERN_DATETIME)
    val start: LocalDateTime,

    @JsonFormat(pattern = PATTERN_DATETIME)
    val end: LocalDateTime
)
