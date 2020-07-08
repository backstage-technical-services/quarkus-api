package org.backstage.auth

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.quarkus.security.UnauthorizedException
import io.quarkus.security.identity.SecurityIdentity
import java.util.*

class AuthorisationTests : BehaviorSpec() {
    init {
        Given("a standard user") {
            val user = mock<SecurityIdentity> {
                on { hasRole(Roles.ROLE_MEMBER) } doReturn false
                on { hasRole(Roles.ROLE_COMMITTEE) } doReturn false
                on { hasRole(Roles.ROLE_SUPER_ADMIN) } doReturn false
            }

            And("a policy that denies access") {
                val policy = createPolicy(user)

                When("testing if authorised with no entity") {
                    Then("an exception should be thrown") {
                        shouldThrow<UnauthorizedException> {
                            policy.authorise("auth", null)
                        }
                    }
                }
                When("testing if authorised with an entity") {
                    Then("an exception should be thrown") {
                        shouldThrow<UnauthorizedException> {
                            policy.authorise("auth", ENTITY)
                        }
                    }
                }
            }
        }

        Given("a committee member") {
            val user = mock<SecurityIdentity> {
                on { hasRole(Roles.ROLE_MEMBER) } doReturn false
                on { hasRole(Roles.ROLE_COMMITTEE) } doReturn true
                on { hasRole(Roles.ROLE_SUPER_ADMIN) } doReturn false
            }

            And("a policy that denies access") {
                val policy = createPolicy(user)

                When("testing if authorised with no entity") {
                    Then("an exception should not be thrown") {
                        shouldNotThrow<UnauthorizedException> {
                            policy.authorise("auth", null)
                        }
                    }
                }
                When("testing if authorised with an entity") {
                    Then("an exception should not be thrown") {
                        shouldNotThrow<UnauthorizedException> {
                            policy.authorise("auth", ENTITY)
                        }
                    }
                }
            }
        }

        Given("a super admin") {
            val user = mock<SecurityIdentity> {
                on { hasRole(Roles.ROLE_MEMBER) } doReturn false
                on { hasRole(Roles.ROLE_COMMITTEE) } doReturn false
                on { hasRole(Roles.ROLE_SUPER_ADMIN) } doReturn true
            }

            And("a policy that denies access") {
                val policy = createPolicy(user)

                When("testing if authorised with no entity") {
                    Then("an exception should not be thrown") {
                        shouldNotThrow<UnauthorizedException> {
                            policy.authorise("auth", null)
                        }
                    }
                }
                When("testing if authorised with an entity") {
                    Then("an exception should not be thrown") {
                        shouldNotThrow<UnauthorizedException> {
                            policy.authorise("auth", ENTITY)
                        }
                    }
                }
            }
        }
    }

    private fun createPolicy(user: SecurityIdentity) = object : Policy<ExampleEntity>(user) {
        override fun authorise(action: Any, entity: ExampleEntity?) = when (entity) {
            null -> authorise { false }
            else -> authorise(entity) { false }
        }
    }

    companion object {
        private val ENTITY = ExampleEntity(createdBy = UUID.randomUUID())
    }
}
