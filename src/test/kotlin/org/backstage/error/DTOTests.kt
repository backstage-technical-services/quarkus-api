package org.backstage.error

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.assertions.json.shouldNotContainJsonKey
import io.kotest.core.spec.style.BehaviorSpec
import org.backstage.error.ValidationError.Constraint
import org.backstage.util.objectMapper
import java.time.LocalDateTime

class ErrorResponseSerialisationTests : BehaviorSpec() {
    init {
        Given("a general error response") {
            val errorResponse = GENERAL_ERROR

            When("serialising the response") {
                val serialisedError = objectMapper.writeValueAsString(errorResponse)

                Then("the serialised response should match the expected JSON") {
                    serialisedError shouldMatchJson """
                        {
                            "timestamp": "2020-01-01 00:00:00",
                            "code": "ERROR_CODE",
                            "message": "An example message"
                        }
                    """.trimIndent()
                }
            }
        }

        Given("a general error response with no message") {
            val errorResponse = GENERAL_ERROR.copy(
                message = null
            )

            When("serialising the response") {
                val serialisedError = objectMapper.writeValueAsString(errorResponse)

                Then("the message key should be missing") {
                    serialisedError shouldNotContainJsonKey "message"
                }
            }
        }

        Given("a validation error DTO") {
            val errorResponse = VALIDATION_ERROR

            When("serialising the response") {
                val serialisedError = objectMapper.writeValueAsString(errorResponse)

                Then("the serialised response should match the expected JSON") {
                    serialisedError shouldMatchJson """
                        {
                            "property": "prop",
                            "value": "val",
                            "constraint": {
                                "name": "${ErrorCode.UNKNOWN_ENUM_VALUE}",
                                "messageKey": "org.backstage.constraints.${ErrorCode.UNKNOWN_ENUM_VALUE}.message",
                                "messageBundle": "org/backstage/messages",
                                "messageParams": {
                                    "value": "val"
                                }
                            }
                        }
                    """.trimIndent()
                }
            }
        }

        Given("a validation error with no constraint") {
            val errorResponse = VALIDATION_ERROR.copy(constraint = null)

            When("serialising the response") {
                val serialisedError = objectMapper.writeValueAsString(errorResponse)

                Then("the serialised response should not contain the constraint key") {
                    serialisedError.shouldNotContainJsonKey("constraint")
                }
            }
        }
    }

    companion object {
        private val GENERAL_ERROR = GeneralError(
            timestamp = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
            code = "ERROR_CODE",
            message = "An example message"
        )
        private val VALIDATION_ERROR = ValidationError(
            property = "prop",
            value = "val",
            constraint = Constraint(
                name = ErrorCode.UNKNOWN_ENUM_VALUE,
                messageParams = mapOf(
                    "value" to "val"
                )
            )
        )
    }
}
