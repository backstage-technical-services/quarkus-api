package org.backstage.awards

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase
import org.backstage.auth.HasAuthor
import org.backstage.util.BaseEntity
import org.hibernate.envers.Audited
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@ApplicationScoped
class AwardRepository : PanacheRepositoryBase<AwardEntity, UUID>

@Audited
@Entity
@Table(name = "awards")
data class AwardEntity(
    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String?,

    @Column(name = "recurring", nullable = false)
    var recurring: Boolean,

    @Column(name = "suggested_by", nullable = false)
    var suggestedBy: String,

    @Column(name = "approved", nullable = false)
    var approved: Boolean
) : BaseEntity(), HasAuthor {
    override val authorId: Any
        get() = suggestedBy
}
