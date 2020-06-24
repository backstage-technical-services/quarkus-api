package org.backstage.util

import io.quarkus.hibernate.orm.panache.PanacheEntityBase
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase
import io.quarkus.security.identity.SecurityIdentity
import org.backstage.auth.getUserId
import org.hibernate.envers.DefaultRevisionEntity
import org.hibernate.envers.RevisionEntity
import org.hibernate.envers.RevisionListener
import javax.enterprise.inject.spi.CDI
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

fun <E, I> PanacheRepositoryBase<E, I>.update(entity: E) = persist(entity)

fun <E : PanacheEntityBase, I> E.update(repository: PanacheRepositoryBase<E, I>) = repository.update(this)

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
