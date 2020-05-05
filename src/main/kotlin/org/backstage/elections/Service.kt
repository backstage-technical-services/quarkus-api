package org.backstage.elections

import org.backstage.error.ExceptionFactory
import org.backstage.error.exceptionWithMessage
import org.backstage.util.SimpleCrudService
import org.backstage.util.update
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional
import javax.ws.rs.core.Response.Status.NOT_FOUND
import kotlin.reflect.KClass

interface ElectionService : SimpleCrudService<CreateElection, UpdateElection>

@ApplicationScoped
class PanacheElectionService(
    private val repository: ElectionRepository
) : ElectionService {
    override fun <T : Any> list(responseClass: KClass<T>): List<T> =
        repository.listAll()
            .map { election -> election.toClass(responseClass) }

    override fun <T : Any> get(id: Long, responseClass: KClass<T>): T =
        find(id)
            .toClass(responseClass)

    @Transactional
    override fun create(request: CreateElection): Long =
        ElectionConverter.createEntity(request)
            .also { election -> repository.persist(election) }
            .let { election -> election.id ?: throw ExceptionFactory.couldNotAssignId(election) }

    @Transactional
    override fun update(id: Long, request: UpdateElection) =
        find(id)
            .modify(request)
            .let { election -> repository.update(election) }

    @Transactional
    override fun delete(id: Long) {
        repository.deleteById(id)
    }

    private fun find(id: Long) =
        repository.findByIdOptional(id)
            .orElseThrow { NOT_FOUND exceptionWithMessage "Could not find election with ID: $id" }
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> ElectionEntity.toClass(clazz: KClass<T>): T = when (clazz) {
    ElectionEntity::class -> this as T
    ElectionResponse::class -> ElectionConverter.toResponse(this) as T
    else -> throw IllegalArgumentException("Cannot convert ${this::class} to $clazz")
}

fun ElectionEntity.modify(request: UpdateElection) = this.apply {
    this.type = request.type
    this.nominationsStart = request.nominations.start
    this.nominationsEnd = request.nominations.end
    this.votingStart = request.voting.start
    this.votingEnd = request.voting.end
    this.hustingsStart = request.hustingsStart
    this.hustingsLocation = request.hustingsLocation
    this.bathStudentId = request.bathStudentId
}
