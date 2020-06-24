package org.backstage

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal
import io.quarkus.security.identity.SecurityIdentity

object AuthHelpers {
    const val DEFAULT_USER_ID = "USER_ID"

    fun createMockedIdentity(userId: String = DEFAULT_USER_ID): SecurityIdentity {
        val principle = mock<OidcJwtCallerPrincipal> {
            on { subject } doReturn userId
        }
        return mock {
            on { principal } doReturn principle
        }
    }
}
