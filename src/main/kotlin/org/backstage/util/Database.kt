package org.backstage.util

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase
import io.quarkus.security.identity.SecurityIdentity
import org.apache.commons.lang3.StringUtils
import org.backstage.auth.getUserId
import org.backstage.error.exceptionWithMessage
import org.hibernate.envers.DefaultRevisionEntity
import org.hibernate.envers.RevisionEntity
import org.hibernate.envers.RevisionListener
import java.util.*
import javax.enterprise.inject.spi.CDI
import javax.persistence.*
import javax.ws.rs.core.Response
import kotlin.reflect.KClass

inline fun <reified E : BaseEntity> KClass<E>.formatClassName(): String =
    this.java.simpleName
        .removeSuffix("Entity")
        .let(StringUtils::splitByCharacterTypeCamelCase)
        .joinToString(separator = " ")
        .toLowerCase()

inline fun <reified E : BaseEntity> PanacheRepositoryBase<E, UUID>.findByIdOrThrow(id: UUID): E = findByIdOptional(id)
    .orElseThrow {
        Response.Status.NOT_FOUND exceptionWithMessage "Could not find ${E::class.formatClassName()} with ID $id"
    }

fun <E : BaseEntity> PanacheRepositoryBase<E, UUID>.update(entity: E) = persist(entity)

fun <E : BaseEntity> E.update(repository: PanacheRepositoryBase<E, UUID>) = repository.update(this)

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    open lateinit var id: UUID
}

@Entity
@Table(name = "revision_info")
@RevisionEntity(RevisionInfoEntityListener::class)
class RevisionInfoEntity(
    @Column(name = "user_id", nullable = false)
    var userId: String
) : DefaultRevisionEntity()

class RevisionInfoEntityListener : RevisionListener {
    private val identity = CDI.current().select(SecurityIdentity::class.java).get()

    override fun newRevision(revisionEntity: Any?) {
        (revisionEntity as? RevisionInfoEntity)?.apply {
            userId = identity.getUserId()
        }
    }
}
