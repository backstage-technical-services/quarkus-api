package org.backstage.elections

import com.fasterxml.jackson.annotation.JsonFormat
import org.backstage.elections.position.CreateElectionPosition
import org.backstage.util.DateTimeBand
import org.backstage.util.PATTERN_DATETIME
import org.valiktor.functions.isNotEmpty
import org.valiktor.functions.isNotNull
import org.valiktor.validate
import java.time.LocalDateTime
import java.util.*

data class ElectionResponse(
    val id: Long,

    val type: ElectionType,

    val nominations: DateTimeBand,

    val voting: DateTimeBand,

    @JsonFormat(pattern = PATTERN_DATETIME)
    val hustingsStart: LocalDateTime?,

    val hustingsLocation: String?,

    val bathStudentId: String?,

    val positions: List<ElectionPositionResponse>
)

data class ElectionPositionResponse(
    val id: Long,

    val name: String
)

data class CreateElection(
    val type: ElectionType,

    val nominations: DateTimeBand,

    val voting: DateTimeBand,

    @JsonFormat(pattern = PATTERN_DATETIME)
    val hustingsStart: LocalDateTime?,

    val hustingsLocation: String? = null,

    val bathStudentId: String? = null,

    val positions: List<CreateElectionPosition>
) {
    init {
        validate(this) {
            validate(CreateElection::type)
                .isNotNull()
            // TODO
            validate(CreateElection::positions)
                .isNotNull()
                .isNotEmpty()
        }
    }
}

data class UpdateElection(
    val type: ElectionType,

    val nominations: DateTimeBand,

    val voting: DateTimeBand,

    @JsonFormat(pattern = PATTERN_DATETIME)
    val hustingsStart: LocalDateTime?,

    val hustingsLocation: String? = null,

    val bathStudentId: String? = null
)

