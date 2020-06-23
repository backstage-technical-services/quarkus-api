package org.backstage

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal
import io.quarkus.security.identity.SecurityIdentity

object AuthHelpers {
    fun createMockedIdentity(userId: String = "USER_ID"): SecurityIdentity {
        val principle = mock<OidcJwtCallerPrincipal> {
            on { subject } doReturn userId
        }
        return mock {
            on { principal } doReturn principle
        }
    }
}
