package org.backstage.awards

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.quarkus.security.UnauthorizedException
import io.quarkus.test.junit.QuarkusTest
import org.backstage.AuthHelpers
import org.backstage.auth.Roles
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import java.util.*

@QuarkusTest
class AuthorisationTests {
    private val identity = AuthHelpers.createMockedIdentity()
    private val repository: AwardRepository = mock {
        on { persist(any<AwardEntity>()) } doAnswer (::mockPersist)
        on { findByIdOptional(any()) } doAnswer { Optional.of(AwardFixtures.HYDRATED_ENTITY) }
        on { delete(any<AwardEntity>()) } doAnswer {}
    }
    private val service: AwardService = RepositoryAwardService(repository, identity)

    @Test
    fun `Given a user with no roles, listing the awards should throw an exception`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(false)

        shouldThrow<UnauthorizedException> {
            service.list(AwardResponse.Full::class)
        }
    }

    @Test
    fun `Given a user who is a member, listing the awards should be successful`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(true)

        shouldNotThrow<UnauthorizedException> {
            service.list(AwardResponse.Full::class)
        }
    }

    @Test
    fun `Given a user with no roles, creating an award should throw an exception`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(false)

        shouldThrow<UnauthorizedException> {
            service.create(AwardFixtures.CREATE_REQUEST)
        }
    }

    @Test
    fun `Given a user who is a member, creating an award should be successful`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(true)

        shouldNotThrow<UnauthorizedException> {
            service.create(AwardFixtures.CREATE_REQUEST)
        }
    }

    @Test
    fun `Given a user with no roles, getting an award should throw an exception`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(false)

        shouldThrow<UnauthorizedException> {
            service.get(UUID.randomUUID(), AwardResponse.Full::class)
        }
    }

    @Test
    fun `Given a user who is a member, getting an award should be successful`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(true)

        shouldNotThrow<UnauthorizedException> {
            service.get(UUID.randomUUID(), AwardResponse.Full::class)
        }
    }

    @Test
    fun `Given a user with no roles, updating an award should throw an exception`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(false)

        shouldThrow<UnauthorizedException> {
            service.update(UUID.randomUUID(), AwardFixtures.UPDATE_REQUEST)
        }
    }

    @Test
    fun `Given a user who is a member, updating an award should throw an exception`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(true)

        shouldThrow<UnauthorizedException> {
            service.update(UUID.randomUUID(), AwardFixtures.UPDATE_REQUEST)
        }
    }

    @Test
    fun `Given a user who is a committee member, updating an award should be successful`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_COMMITTEE)).thenReturn(true)

        shouldNotThrow<UnauthorizedException> {
            service.update(UUID.randomUUID(), AwardFixtures.UPDATE_REQUEST)
        }
    }

    @Test
    fun `Given a user who is a super admin, updating an award should be successful`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_SUPER_ADMIN)).thenReturn(true)

        shouldNotThrow<UnauthorizedException> {
            service.update(UUID.randomUUID(), AwardFixtures.UPDATE_REQUEST)
        }
    }

    @Test
    fun `Given a user with no roles, approving an award should throw an exception`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(false)

        shouldThrow<UnauthorizedException> {
            service.approve(UUID.randomUUID())
        }
    }

    @Test
    fun `Given a user who is a member, approving an award should throw an exception`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(true)

        shouldThrow<UnauthorizedException> {
            service.approve(UUID.randomUUID())
        }
    }

    @Test
    fun `Given a user who is a committee member, approving an award should be successful`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_COMMITTEE)).thenReturn(true)

        shouldNotThrow<UnauthorizedException> {
            service.approve(UUID.randomUUID())
        }
    }

    @Test
    fun `Given a user who is a super admin, approving an award should be successful`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_SUPER_ADMIN)).thenReturn(true)

        shouldNotThrow<UnauthorizedException> {
            service.approve(UUID.randomUUID())
        }
    }

    @Test
    fun `Given a user with no roles, unapproving an award should throw an exception`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(false)

        shouldThrow<UnauthorizedException> {
            service.unapprove(UUID.randomUUID())
        }
    }

    @Test
    fun `Given a user who is a member, unapproving an award should throw an exception`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(true)

        shouldThrow<UnauthorizedException> {
            service.unapprove(UUID.randomUUID())
        }
    }

    @Test
    fun `Given a user who is a committee member, unapproving an award should be successful`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_COMMITTEE)).thenReturn(true)

        shouldNotThrow<UnauthorizedException> {
            service.unapprove(UUID.randomUUID())
        }
    }

    @Test
    fun `Given a user who is a super admin, unapproving an award should be successful`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_SUPER_ADMIN)).thenReturn(true)

        shouldNotThrow<UnauthorizedException> {
            service.unapprove(UUID.randomUUID())
        }
    }

    @Test
    fun `Given a user with no roles, deleting an award should throw an exception`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(false)

        shouldThrow<UnauthorizedException> {
            service.delete(UUID.randomUUID())
        }
    }

    @Test
    fun `Given a user who is a member, deleting an award should throw an exception`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(true)

        shouldThrow<UnauthorizedException> {
            service.delete(UUID.randomUUID())
        }
    }

    @Test
    fun `Given a user who is a committee member, deleting an award should be successful`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_COMMITTEE)).thenReturn(true)

        shouldNotThrow<UnauthorizedException> {
            service.delete(UUID.randomUUID())
        }
    }

    @Test
    fun `Given a user who is a super admin, deleting an award should be successful`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_SUPER_ADMIN)).thenReturn(true)

        shouldNotThrow<UnauthorizedException> {
            service.delete(UUID.randomUUID())
        }
    }

    private fun mockPersist(mock: InvocationOnMock): Unit? {
        with(mock.getArgument(0, AwardEntity::class.java)) {
            id = UUID.randomUUID()
        }

        return Unit
    }
}
