package org.backstage.elections

import io.quarkus.hibernate.orm.panache.PanacheEntity
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase
import org.backstage.elections.nomination.ElectionNominationEntity
import org.backstage.elections.position.ElectionPositionEntity
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.persistence.*

@ApplicationScoped
class ElectionRepository : PanacheRepositoryBase<ElectionEntity, Long>

@Entity
@Table(name = "election")
class ElectionEntity(
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: ElectionType,

    @Column(name = "nominations_start", nullable = false)
    var nominationsStart: LocalDateTime,

    @Column(name = "nominations_end", nullable = false)
    var nominationsEnd: LocalDateTime,

    @Column(name = "voting_start", nullable = false)
    var votingStart: LocalDateTime,

    @Column(name = "voting_end", nullable = false)
    var votingEnd: LocalDateTime,

    @Column(name = "hustings_start")
    var hustingsStart: LocalDateTime? = null,

    @Column(name = "hustings_location")
    var hustingsLocation: String? = null,

    @Column(name = "bathstudent_id")
    var bathStudentId: String? = null
) : PanacheEntity() {
    @OneToMany(mappedBy = "election", cascade = [CascadeType.ALL])
    var positions: MutableList<ElectionPositionEntity> = mutableListOf()

    @OneToMany(mappedBy = "election")
    var nominations: MutableList<ElectionNominationEntity> = mutableListOf()

    fun add(position: ElectionPositionEntity) {
        positions.add(position)
        position.election = this
    }

    fun add(nomination: ElectionNominationEntity) {
        nominations.add(nomination)
        nomination.election = this
    }
}

enum class ElectionType {
    FULL,
    BY_ELECTION
}
