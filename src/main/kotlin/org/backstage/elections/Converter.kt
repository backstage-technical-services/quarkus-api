package org.backstage.elections

import org.backstage.elections.position.ElectionPositionConverter
import org.backstage.util.DateTimeBand

interface ElectionConverter {
    companion object {
        fun createEntity(request: CreateElection): ElectionEntity = ElectionEntity(
            type = request.type,
            nominationsStart = request.nominations.start,
            nominationsEnd = request.nominations.end,
            votingStart = request.voting.start,
            votingEnd = request.voting.end,
            hustingsStart = request.hustingsStart,
            hustingsLocation = request.hustingsLocation,
            bathStudentId = request.bathStudentId
        ).apply {
            request.positions.map(ElectionPositionConverter.Companion::createEntity)
                .forEach(this::add)
        }

        fun toResponse(election: ElectionEntity) = ElectionResponse(
            id = election.id,
            type = election.type,
            nominations = DateTimeBand(
                start = election.nominationsStart,
                end = election.nominationsEnd
            ),
            voting = DateTimeBand(
                start = election.votingStart,
                end = election.votingEnd
            ),
            hustingsStart = election.hustingsStart,
            hustingsLocation = election.hustingsLocation,
            bathStudentId = election.bathStudentId,
            positions = election.positions.map(ElectionPositionConverter.Companion::toResponse)
        )
    }
}

