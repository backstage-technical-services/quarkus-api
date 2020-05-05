package org.backstage.elections.position

import org.backstage.elections.ElectionEntity
import org.backstage.error.exceptionWithMessage
import org.backstage.util.update
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional
import javax.ws.rs.core.Response.Status.NOT_FOUND

interface ElectionPositionService {
    fun create(election: ElectionEntity, request: CreateElectionPosition): Long

    fun update(election: ElectionEntity, positionId: Long, request: UpdateElectionPosition)

    fun delete(election: ElectionEntity, positionId: Long)
}

@ApplicationScoped
class PanacheElectionPositionService(
    private val repository: ElectionPositionRepository
) : ElectionPositionService {
    @Transactional
    override fun create(election: ElectionEntity, request: CreateElectionPosition): Long =
        ElectionPositionConverter.createEntity(request)
            .also { electionPosition -> electionPosition.election = election }
            .also { electionPosition -> repository.persist(electionPosition) }
            .id

    @Transactional
    override fun update(election: ElectionEntity, positionId: Long, request: UpdateElectionPosition) =
        find(positionId)
            .modify(request)
            .let { electionPosition -> repository.update(electionPosition) }

    @Transactional
    override fun delete(election: ElectionEntity, positionId: Long) {
        repository.deleteById(positionId)
    }

    private fun find(id: Long) =
        repository.findByIdOptional(id)
            .orElseThrow { NOT_FOUND exceptionWithMessage "Could not find election position with ID: $id" }
}

fun ElectionPositionEntity.modify(request: UpdateElectionPosition) = apply {
    this.name = request.name
}
