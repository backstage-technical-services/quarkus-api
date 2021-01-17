package org.backstage.menu

import io.quarkus.security.identity.SecurityIdentity
import org.backstage.auth.isAdmin
import javax.enterprise.context.ApplicationScoped

interface MenuService {
    fun getMain(): MainMenu

    fun getAdmin(): AdminMenu?
}

@ApplicationScoped
class IdentityBasedBuilderMenuService(
    private val identity: SecurityIdentity
) : MenuService {
    override fun getMain(): MainMenu = MainMenuBuilder.build(identity)

    override fun getAdmin(): AdminMenu? = when (identity.isAdmin()) {
        true -> AdminMenuBuilder.build(identity)
        else -> null
    }
}
