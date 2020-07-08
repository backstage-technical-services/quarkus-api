package org.backstage

import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal
import io.quarkus.security.identity.SecurityIdentity
import java.security.Principal

object AuthHelpers {
    const val DEFAULT_USER_ID = "USER_ID"

    fun createMockedPrincipal(userId: String = DEFAULT_USER_ID): Principal =
        mock<OidcJwtCallerPrincipal> {
            on { subject } doReturn userId
        }

    fun createMockedIdentity(userId: String = DEFAULT_USER_ID): SecurityIdentity =
         mock {
            on { principal } doAnswer { createMockedPrincipal(userId) }
        }
}
