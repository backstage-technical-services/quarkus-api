package org.backstage.elections.nomination

import io.quarkus.hibernate.orm.panache.PanacheEntity
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase
import org.backstage.elections.ElectionEntity
import org.backstage.elections.position.ElectionPositionEntity
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.*

@ApplicationScoped
class ElectionNominationRepository : PanacheRepositoryBase<ElectionNominationEntity, Long>

@Entity
@Table(name = "election_nomination")
class ElectionNominationEntity(
    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    var election: ElectionEntity,

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    var position: ElectionPositionEntity,

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    var user: UUID, // TODO

    @Column(name = "is_elected", nullable = false)
    var elected: Boolean
) : PanacheEntity()
