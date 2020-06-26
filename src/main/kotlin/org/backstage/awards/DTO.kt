package org.backstage.awards

import com.fasterxml.jackson.annotation.JsonProperty
import org.valiktor.functions.isNotBlank
import org.valiktor.functions.isNotNull
import org.valiktor.validate
import java.util.*

sealed class AwardRequest {
    data class Create(
        @JsonProperty("name")
        val name: String,

        @JsonProperty("description")
        val description: String?,

        @JsonProperty("recurring")
        val recurring: Boolean
    ) : AwardRequest() {
        init {
            validate(this) {
                validate(Create::name)
                    .isNotNull()
                    .isNotBlank()
                validate(Create::description)
                    .isNotBlank()
                validate(Create::recurring)
                    .isNotNull()
            }
        }
    }

    data class Update(
        @JsonProperty("name")
        val name: String,

        @JsonProperty("description")
        val description: String?,

        @JsonProperty("recurring")
        val recurring: Boolean
    ) : AwardRequest() {
        init {
            validate(this) {
                validate(Update::name)
                    .isNotNull()
                    .isNotBlank()
                validate(Update::description)
                    .isNotBlank()
                validate(Update::recurring)
                    .isNotNull()
            }
        }
    }
}

sealed class AwardResponse {
    data class Full(
        @JsonProperty("id")
        val id: UUID,

        @JsonProperty("name")
        val name: String,

        @JsonProperty("description")
        val description: String?,

        @JsonProperty("recurring")
        val recurring: Boolean,

        @JsonProperty("suggestedBy")
        val suggestedBy: String,

        @JsonProperty("approved")
        val approved: Boolean
    ) : AwardResponse()
}
