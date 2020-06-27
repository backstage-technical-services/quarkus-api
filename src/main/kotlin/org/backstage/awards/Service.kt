package org.backstage.awards

import io.quarkus.security.identity.SecurityIdentity
import org.backstage.auth.Policy
import org.backstage.util.CrudOperations
import org.backstage.util.SimpleCrudService
import org.backstage.util.findByIdOrThrow
import org.backstage.util.update
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional
import kotlin.reflect.KClass

interface AwardService : SimpleCrudService<AwardRequest.Create, AwardRequest.Update> {
    fun approve(id: UUID)

    fun unapprove(id: UUID)
}

@ApplicationScoped
class RepositoryAwardService(
    private val repository: AwardRepository,
    private val identity: SecurityIdentity
) : AwardService {
    private val policy = object : Policy<AwardEntity>(identity) {
        override fun authorise(action: Any, entity: AwardEntity?) = when (action) {
            CrudOperations.LIST,
            CrudOperations.CREATE,
            CrudOperations.VIEW -> authorise { identity.isMember() }
            CrudOperations.UPDATE,
            CrudOperations.DELETE -> authorise { identity.isAdmin() }
            else -> deny()
        }
    }

    override fun <T : Any> list(responseClass: KClass<T>): List<T> = policy.authoriseAndDo(CrudOperations.LIST) {
        repository
            .listAll()
            .map { award -> award.toClass(responseClass) }
            .toList()
    }

    @Transactional
    override fun create(request: AwardRequest.Create): UUID = policy.authoriseAndDo(CrudOperations.CREATE) {
        AwardConverter
            .toEntity(request, identity)
            .also { entity -> repository.persist(entity) }
            .id
    }

    override fun <T : Any> get(id: UUID, responseClass: KClass<T>): T = repository
        .findByIdOrThrow(id)
        .also { award -> policy.authorise(CrudOperations.VIEW, award) }
        .toClass(responseClass)

    @Transactional
    override fun update(id: UUID, request: AwardRequest.Update) = repository
        .findByIdOrThrow(id)
        .also { award -> policy.authorise(CrudOperations.UPDATE, award) }
        .modify(request)
        .update(repository)

    @Transactional
    override fun approve(id: UUID) = updateApproval(id, true)

    @Transactional
    override fun unapprove(id: UUID) = updateApproval(id, false)

    @Transactional
    override fun delete(id: UUID) = repository
        .findByIdOrThrow(id)
        .also { award -> policy.authorise(CrudOperations.DELETE, award) }
        .let { award -> repository.delete(award) }

    private fun updateApproval(id: UUID, approve: Boolean) = repository
        .findByIdOrThrow(id)
        .also { award -> policy.authorise(CrudOperations.UPDATE, award) }
        .apply { approved = approve }
        .update(repository)
}

fun AwardEntity.modify(request: AwardRequest.Update) = apply {
    name = request.name
    description = request.description
    recurring = request.recurring
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> AwardEntity.toClass(responseClass: KClass<T>) = when (responseClass) {
    AwardResponse.Full::class -> AwardConverter.toResponse<AwardResponse.Full>(this) as T
    else -> throw IllegalArgumentException("Cannot convert ${AwardEntity::class} to $responseClass")
}
