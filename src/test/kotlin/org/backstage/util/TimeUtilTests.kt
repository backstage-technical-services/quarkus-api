package org.backstage.util

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DateTimeBandSerialisationTests : BehaviorSpec() {
    init {
        Given("an expected time band and JSON") {
            val timeBand = TimeBand(
                start = LocalTime.of(8, 30, 30),
                end = LocalTime.of(18, 59, 59)
            )
            val timeBandJson = """
                {
                    "start": "08:30:30",
                    "end": "18:59:59"
                }
            """.trimIndent()

            When("serialising the DTO") {
                val serialisedTimeBand = objectMapper.writeValueAsString(timeBand)

                Then("the serialised JSON should match") {
                    serialisedTimeBand shouldMatchJson timeBandJson
                }
            }

            When("de-serialising the JSON") {
                val deserialisedTimeBand = objectMapper.readValue<TimeBand>(timeBandJson)

                Then("the DTO should match") {
                    deserialisedTimeBand shouldBe timeBand
                }
            }
        }

        Given("an expected date band and JSON") {
            val dateBand = DateBand(
                start = LocalDate.of(2020, 1, 1),
                end = LocalDate.of(2020, 2, 1)
            )
            val dateBandJson = """
                {
                    "start": "2020-01-01",
                    "end": "2020-02-01"
                }
            """.trimIndent()

            When("serialising the DTO") {
                val serialisedDateBand = objectMapper.writeValueAsString(dateBand)

                Then("the serialised JSON should match") {
                    serialisedDateBand shouldMatchJson dateBandJson
                }
            }

            When("de-serialising the JSON") {
                val deserialisedDateBand = objectMapper.readValue<DateBand>(dateBandJson)

                Then("the DTO should match") {
                    deserialisedDateBand shouldBe dateBand
                }
            }
        }

        Given("an expected datetime band and JSON") {
            val dateTimeBand = DateTimeBand(
                start = LocalDateTime.of(2020, 1, 1, 8, 30, 30),
                end = LocalDateTime.of(2020, 2, 1, 18, 59, 59)
            )
            val dateTimeBandJson = """
                {
                    "start": "2020-01-01 08:30:30",
                    "end": "2020-02-01 18:59:59"
                }
            """.trimIndent()

            When("serialising the DTO") {
                val serialisedDateTimeBand = objectMapper.writeValueAsString(dateTimeBand)

                Then("the serialised JSON should match") {
                    serialisedDateTimeBand shouldMatchJson dateTimeBandJson
                }
            }

            When("de-serialising the JSON") {
                val deserialisedDateTimeBand = objectMapper.readValue<DateTimeBand>(dateTimeBandJson)

                Then("the DTO should match") {
                    deserialisedDateTimeBand shouldBe dateTimeBand
                }
            }
        }
    }
}
