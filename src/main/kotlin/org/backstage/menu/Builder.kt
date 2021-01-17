package org.backstage.menu

import io.quarkus.security.identity.SecurityIdentity

interface MenuBuilder<T> {
    fun build(user: SecurityIdentity): T
}

object MainMenuBuilder : MenuBuilder<MainMenu> {
    override fun build(user: SecurityIdentity): MainMenu = listOf(
        MenuItem.Main(text = "Events", link = MenuLink(alias = "events.diary")),
        MenuItem.Main(text = "Members", link = MenuLink(alias = "members.dashboard")),
        MenuItem.Main(
            text = "Equipment",
            children = listOf(
                MenuItem.Main(text = "Asset database", link = MenuLink(alias = "equipment.assets")),
                MenuItem.Main(text = "Repairs database", link = MenuLink(alias = "equipment.repairs"))
            )
        ),
        MenuItem.Main(
            text = "Training",
            children = listOf(
                MenuItem.Main(text = "Skills", link = MenuLink(alias = "training.skills")),
                MenuItem.Main(text = "Skill applications", link = MenuLink(alias = "training.skill.applications")),
                MenuItem.Main(text = "Skill categories", link = MenuLink(alias = "training.skill.categories"))
            )
        ),
        MenuItem.Main(
            text = "Safety",
            children = listOf(
                MenuItem.Main(text = "Report incident", link = MenuLink(alias = "report.incident")),
                MenuItem.Main(text = "Report near miss", link = MenuLink(alias = "report.nearmiss"))
            )
        ),
        MenuItem.Main(text = "Resources", link = MenuLink(alias = "resources.search"))
    )
}

object AdminMenuBuilder : MenuBuilder<AdminMenu> {
    override fun build(user: SecurityIdentity): AdminMenu = listOf(
        MenuItem.Admin(icon = "cogs", text = "Dashboard", link = MenuLink(alias = "admin.dashboard")),
        MenuItem.Admin(
            icon = "users",
            text = "Users",
            items = listOf(
                MenuItem.Admin(icon = "users", text = "View users", link = MenuLink(alias = "admin.users")),
                MenuItem.Admin(icon = "user-plus", text = "Add users", link = MenuLink(alias = "admin.users.add")),
                MenuItem.Admin(icon = "user-friends", text = "Manage groups", link = MenuLink(alias = "admin.groups")),
                MenuItem.Admin(icon = "users-crown", text = "The committee", link = MenuLink(alias = "admin.committee"))
            )
        )
    )
}
