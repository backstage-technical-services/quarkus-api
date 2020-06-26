package org.backstage.awards

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import org.backstage.AuthHelpers
import org.backstage.auth.Roles.ROLE_MEMBER
import org.backstage.auth.Roles.ROLE_SUPER_ADMIN
import org.backstage.error.HttpException
import org.junit.jupiter.api.*
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.ws.rs.core.Response.Status.NOT_FOUND

@QuarkusTest
@TestInstance(PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class ServiceTests {
    @Inject
    private lateinit var repository: AwardRepository

    @Inject
    private lateinit var service: AwardService

    @InjectMock
    private lateinit var identity: SecurityIdentity

    private lateinit var awardId: UUID

    @BeforeEach
    fun setup() {
        Mockito.`when`(identity.hasRole(ROLE_MEMBER)).thenReturn(true)
        Mockito.`when`(identity.hasRole(ROLE_SUPER_ADMIN)).thenReturn(true)
        Mockito.`when`(identity.principal).thenAnswer { AuthHelpers.createMockedPrincipal() }
    }

    @Test
    fun `the service should be injected`() {
        service.shouldNotBeNull()
    }

    @Test
    @Order(1)
    fun `listing the awards, should return all the existing awards`() {
        val awards = service.list(AwardResponse.Full::class)

        awards.shouldHaveSize(1)
    }

    @Test
    @Order(2)
    fun `when creating an award, it should be persisted`() {
        awardId = service.create(AwardFixtures.CREATE_REQUEST)

        awardId.shouldNotBeNull()
        repository.findByIdOptional(awardId).isPresent shouldBe true
    }

    @Test
    @Order(3)
    fun `when getting the details of an award, the correct award should be returned`() {
        val award = service.get(awardId, AwardResponse.Full::class)

        award.id shouldBe awardId
        award.name shouldBe AwardFixtures.CREATE_REQUEST.name
        award.description shouldBe AwardFixtures.CREATE_REQUEST.description
        award.recurring shouldBe AwardFixtures.CREATE_REQUEST.recurring
        award.suggestedBy shouldBe AuthHelpers.DEFAULT_USER_ID
        award.approved shouldBe false
    }

    @Test
    @Order(4)
    fun `when updating an award, the changes should be persisted`() {
        val request = AwardFixtures.UPDATE_REQUEST
        service.update(awardId, request)

        val modifiedAward = repository.findById(awardId)
        modifiedAward.name shouldBe request.name
        modifiedAward.description shouldBe request.description
        modifiedAward.recurring shouldBe request.recurring
    }

    @Test
    @Order(5)
    fun `when marking an award as approved, the change should be persisted`() {
        service.approve(awardId)

        repository.findById(awardId).approved shouldBe true
    }

    @Test
    @Order(6)
    fun `when marking an award as not approved, the change should be persisted`() {
        service.unapprove(awardId)

        repository.findById(awardId).approved shouldBe false
    }

    @Test
    @Order(7)
    fun `when deleting an award, it should be removed from the database`() {
        service.delete(awardId)

        repository.findByIdOptional(awardId).isPresent shouldBe false
    }

    @Test
    fun `when getting the details of an award that doesn't exist, an exception should be thrown`() {
        val exception = shouldThrow<HttpException> {
            service.get(AwardFixtures.NON_EXISTENT_ID, AwardResponse.Full::class)
        }
        exception.status shouldBe NOT_FOUND
    }

    @Test
    fun `when updating the details of an award that doesn't exist, an exception should be thrown`() {
        val exception = shouldThrow<HttpException> {
            service.update(AwardFixtures.NON_EXISTENT_ID, AwardFixtures.UPDATE_REQUEST)
        }
        exception.status shouldBe NOT_FOUND
    }

    @Test
    fun `when approving an award that doesn't exist, an exception should be thrown`() {
        val exception = shouldThrow<HttpException> {
            service.approve(AwardFixtures.NON_EXISTENT_ID)
        }
        exception.status shouldBe NOT_FOUND
    }

    @Test
    fun `when unapproving an award that doesn't exist, an exception should be thrown`() {
        val exception = shouldThrow<HttpException> {
            service.unapprove(AwardFixtures.NON_EXISTENT_ID)
        }
        exception.status shouldBe NOT_FOUND
    }

    @Test
    fun `when deleting an award that doesn't exist, an exception should be thrown`() {
        val exception = shouldThrow<HttpException> {
            service.delete(AwardFixtures.NON_EXISTENT_ID)
        }
        exception.status shouldBe NOT_FOUND
    }
}

class ModifyingTests : BehaviorSpec() {
    init {
        Given("an existing entity and an update request") {
            val entity = AwardFixtures.HYDRATED_ENTITY
            val request = AwardRequest.Update(
                name = "Update Name",
                description = "Update description",
                recurring = !entity.recurring
            )

            When("modifying the entity") {
                entity.modify(request)

                Then("The name should be updated") {
                    entity.name shouldBe request.name
                }
                Then("The description should be updated") {
                    entity.description shouldBe request.description
                }
                Then("The recurring value should be updated") {
                    entity.recurring shouldBe request.recurring
                }
            }
        }
    }
}

class AwardEntityConvertingTests : BehaviorSpec() {
    init {
        Given("an existing entity") {
            val entity = AwardFixtures.HYDRATED_ENTITY

            When("converting to a full response DTO") {
                val response = entity.toClass(AwardResponse.Full::class)

                Then("the response should be the same as from the converter") {
                    response shouldBe AwardConverter.toResponse(entity)
                }
            }
            When("converting to an invalid class") {
                val exception = shouldThrow<IllegalArgumentException> {
                    entity.toClass(String::class)
                }

                Then("the exception should contain the expected message") {
                    exception.message shouldStartWith "Cannot convert"
                    exception.message shouldContain entity::class.toString()
                    exception.message shouldContain String::class.toString()
                }
            }
        }
    }
}
