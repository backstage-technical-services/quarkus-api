package org.backstage.error

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.backstage.util.PATTERN_DATETIME
import java.time.LocalDateTime

data class ValidationError(
    @JsonProperty("property")
    val property: String,

    @JsonProperty("value")
    val value: Any?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("constraint")
    val constraint: Constraint? = null
) {
    data class Constraint(
        @JsonProperty("name")
        val name: String,

        @JsonProperty("messageKey")
        val messageKey: String = "org.backstage.constraints.${name}.message",

        @JsonProperty("messageBundle")
        val messageBundle: String = "org/backstage/messages",

        @JsonProperty("messageParams")
        val messageParams: Map<String, Any?> = mapOf()
    )
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GeneralError(
    @JsonProperty("timestamp")
    @JsonFormat(pattern = PATTERN_DATETIME)
    val timestamp: LocalDateTime = LocalDateTime.now(),

    @JsonProperty("code")
    val code: Any,

    @JsonProperty("message")
    val message: String?
)
