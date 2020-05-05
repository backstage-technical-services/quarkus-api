package org.backstage.elections.position

import org.backstage.elections.ElectionPositionResponse

interface ElectionPositionConverter {
    companion object {
        fun createEntity(electionPosition: CreateElectionPosition) = ElectionPositionEntity(
            name = electionPosition.name
        )

        fun toResponse(position: ElectionPositionEntity) = ElectionPositionResponse(
            id = position.id,
            name = position.name
        )
    }
}
