package org.backstage.elections.position

import io.quarkus.hibernate.orm.panache.PanacheEntity
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase
import org.backstage.elections.ElectionEntity
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.*

@ApplicationScoped
class ElectionPositionRepository : PanacheRepositoryBase<ElectionPositionEntity, Long>

@Entity
@Table(name = "election_position")
class ElectionPositionEntity(
    @Column(name = "name", nullable = false)
    var name: String
) : PanacheEntity() {
    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    lateinit var election: ElectionEntity
}
