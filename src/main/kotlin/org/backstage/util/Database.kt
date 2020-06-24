package org.backstage.util

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase
import io.quarkus.security.identity.SecurityIdentity
import org.backstage.auth.getUserId
import org.hibernate.envers.DefaultRevisionEntity
import org.hibernate.envers.RevisionEntity
import org.hibernate.envers.RevisionListener
import java.util.*
import javax.enterprise.inject.spi.CDI
import javax.persistence.*

fun <E : BaseEntity> PanacheRepositoryBase<E, UUID>.update(entity: E) = persist(entity)

fun <E : BaseEntity> E.update(repository: PanacheRepositoryBase<E, UUID>) = repository.update(this)

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "UUID")
    open lateinit var id: UUID
}

@Entity
@Table(name = "revision_info")
@RevisionEntity(RevisionInfoEntityListener::class)
class RevisionInfoEntity(
    @Column(name = "user_id")
    var userId: String
) : DefaultRevisionEntity()

class RevisionInfoEntityListener : RevisionListener {
    private val identity = CDI.current().select(SecurityIdentity::class.java).get()

    override fun newRevision(revisionEntity: Any?) {
        (revisionEntity as? RevisionInfoEntity?)?.apply {
            userId = identity.getUserId()
        }
    }
}
