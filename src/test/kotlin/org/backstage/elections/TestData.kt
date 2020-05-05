package org.backstage.elections

import org.backstage.elections.position.CreateElectionPosition
import org.backstage.elections.position.ElectionPositionEntity
import org.backstage.util.DateTimeBand
import java.time.LocalDateTime

val nominationStart: LocalDateTime = LocalDateTime.of(2020, 1, 1, 8, 0, 0)
val nominationEnd: LocalDateTime = LocalDateTime.of(2020, 1, 7, 17, 0, 0)
val votingStart: LocalDateTime = LocalDateTime.of(2020, 1, 7, 8, 0, 0)
val votingEnd: LocalDateTime = LocalDateTime.of(2020, 1, 15, 17, 0, 0)
val hustingsStart: LocalDateTime = LocalDateTime.of(2020, 1, 7, 18, 0)
const val hustingsLocation: String = "1E 3.6"
const val bathStudentId: String = "BATH_STUDENT"
val positionNames = listOf(
    "Chair",
    "Secretary",
    "Treasurer",
    "Welfare Officer",
    "Training & Safety Officer",
    "Equipment Officer",
    "Social Secretary"
)

val createRequest = CreateElection(
    type = ElectionType.FULL,
    nominations = DateTimeBand(
        start = nominationStart,
        end = nominationEnd
    ),
    voting = DateTimeBand(
        start = votingStart,
        end = votingEnd
    ),
    hustingsStart = hustingsStart,
    hustingsLocation = hustingsLocation,
    bathStudentId = bathStudentId,
    positions = positionNames.map { name -> CreateElectionPosition(name = name) }
)

val entityHydrated = ElectionEntity(
    type = ElectionType.FULL,
    nominationsStart = nominationStart,
    nominationsEnd = nominationEnd,
    votingStart = votingStart,
    votingEnd = votingEnd,
    hustingsStart = hustingsStart,
    hustingsLocation = hustingsLocation,
    bathStudentId = bathStudentId
).apply {
    this.id = 1
    this.positions = positionNames.map { name ->
        ElectionPositionEntity(name = name).also { position ->
            position.id = 2
            position.election = this
        }
    }.toMutableList()
}

val entity = ElectionEntity(
    type = ElectionType.FULL,
    nominationsStart = nominationStart,
    nominationsEnd = nominationEnd,
    votingStart = votingStart,
    votingEnd = votingEnd,
    hustingsStart = hustingsStart,
    hustingsLocation = hustingsLocation,
    bathStudentId = bathStudentId
).apply {
    this.positions = positionNames.map { name ->
        ElectionPositionEntity(name = name).also { position ->
            position.election = this
        }
    }.toMutableList()
}
