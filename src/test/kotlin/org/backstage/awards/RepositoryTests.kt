package org.backstage.awards

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import org.backstage.AuthHelpers
import org.backstage.error.HttpException
import org.backstage.util.findByIdOrThrow
import org.backstage.util.update
import org.junit.jupiter.api.*
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.core.Response.Status.NOT_FOUND

@QuarkusTest
@TestMethodOrder(OrderAnnotation::class)
@TestInstance(PER_CLASS)
class RepositoryTests {
    @Inject
    private lateinit var repository: AwardRepository

    @InjectMock
    private lateinit var identity: SecurityIdentity

    private val entity = AwardFixtures.ENTITY.copy()

    @BeforeEach
    fun setup() {
        Mockito.`when`(identity.principal).thenAnswer { AuthHelpers.createMockedPrincipal() }
    }

    @Test
    fun `the repository should be injected`() {
        repository.shouldNotBeNull()
    }

    @Test
    @Order(1)
    @Transactional
    fun `Given a new entity, when persisted it should be saved to the database`() {
        repository.persist(entity)
        repository.findById(entity.id).shouldNotBeNull()
    }

    @Test
    @Order(2)
    @Transactional
    fun `Given an existing entity to modify, when updating the changes should be saved to the database`() {
        val existingEntity = repository.findById(entity.id)
        existingEntity.name = "The name has been updated"
        repository.update(existingEntity)

        val modifiedEntity = repository.findById(entity.id)
        modifiedEntity.name shouldBe "The name has been updated"
        modifiedEntity.description shouldBe entity.description
        modifiedEntity.recurring shouldBe entity.recurring
        modifiedEntity.suggestedBy shouldBe entity.suggestedBy
        modifiedEntity.approved shouldBe entity.approved
    }

    @Test
    @Order(3)
    @Transactional
    fun `Given an existing entity, when deleting it should be removed from the database`() {
        repository.deleteById(entity.id)
        repository.findByIdOptional(entity.id).isPresent shouldBe false
    }

    @Test
    @Order(4)
    fun `Given the ID of an existing entity, when retrieving it the entity should be returned`() {
        val existingId = UUID.fromString("14fe4f13-863d-4f92-89b8-80967e705e4e")

        repository.findByIdOrThrow(existingId).shouldNotBeNull()
    }

    @Test
    @Order(5)
    fun `Given the ID of a non-existent entity, when retrieving an exception should be thrown`() {
        val nonExistentId = UUID.fromString("137218ed-a1c3-4b8f-8b79-e7d4e14f7f1b")
        val exception = shouldThrow<HttpException> {
            repository.findByIdOrThrow(nonExistentId)
        }

        exception.status shouldBe NOT_FOUND
        exception.message shouldStartWith "Could not find award with ID"
    }
}
