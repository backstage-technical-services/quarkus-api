package org.backstage.menu

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import org.backstage.AuthHelpers
import org.backstage.auth.Roles
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import javax.inject.Inject

@QuarkusTest
class ServiceTests {
    @InjectMock
    private lateinit var identity: SecurityIdentity

    @Inject
    private lateinit var service: MenuService

    @BeforeEach
    fun setup() {
        Mockito.`when`(identity.principal)
            .thenAnswer { AuthHelpers.createMockedPrincipal() }
    }

    @Test
    fun `Given a non-member and non-admin, when building the main menu, the expected items should be added`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(false)
        Mockito.`when`(identity.hasRole(Roles.ROLE_SUPER_ADMIN)).thenReturn(false)

        val menu = service.getMain()

        menu.shouldHaveSize(1) // TODO
    }

    @Test
    fun `Given a member, when building the main menu, the expected items should be returned`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(true)
        Mockito.`when`(identity.hasRole(Roles.ROLE_SUPER_ADMIN)).thenReturn(false)

        val menu = service.getMain()

        menu.shouldHaveSize(1) // TODO
    }

    @Test
    fun `Given an admin, when building the main menu, the expected items should be returned`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_MEMBER)).thenReturn(false)
        Mockito.`when`(identity.hasRole(Roles.ROLE_SUPER_ADMIN)).thenReturn(true)

        val menu = service.getMain()

        menu.shouldHaveSize(1) // TODO
    }

    @Test
    fun `Given a non admin, when building the admin menu, no menu should be returned`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_SUPER_ADMIN)).thenReturn(false)

        val menu = service.getAdmin()

        menu.shouldBeNull()
    }

    @Test
    fun `Given an admin, when building the admin menu, the expected items should be returned`() {
        Mockito.`when`(identity.hasRole(Roles.ROLE_SUPER_ADMIN)).thenReturn(true)

        val menu = service.getAdmin()

        menu.shouldNotBeNull()
        menu.shouldHaveSize(1) // TODO
    }
}
