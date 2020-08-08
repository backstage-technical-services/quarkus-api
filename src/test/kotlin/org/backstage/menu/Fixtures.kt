package org.backstage.menu

import java.util.*

object MenuFixtures {
    val MENU_LINK_HREF = MenuLink(
        href = "/test#"
    )
    val MENU_LINK_HREF_JSON = """
        {
            "href": "/test#"
        }
    """.trimIndent()
    val MENU_LINK_ALIAS = MenuLink(
        alias = "test"
    )
    val MENU_LINK_ALIAS_JSON = """
        {
            "alias": "test"
        }
    """.trimIndent()

    val MENU_ITEM_MAIN = MenuItem.Main(
        id = UUID.fromString("d72410fc-8361-4f14-9105-d96836f5533f"),
        text = "The Link",
        link = MenuLink(alias = "link"),
        children = listOf(
            MenuItem.Main(
                id = UUID.fromString("5c721ca4-7be1-4316-ae11-2a70800f18d8"),
                text = "The Child",
                link = MenuLink(alias = "child")
            )
        )
    )
    val MENU_ITEM_MAIN_JSON = """
        {
            "id": "d72410fc-8361-4f14-9105-d96836f5533f",
            "text": "The Link",
            "link": {
                "alias": "link"
            },
            "children": [
                {
                    "id": "5c721ca4-7be1-4316-ae11-2a70800f18d8",
                    "text": "The Child",
                    "link": {
                        "alias": "child"
                    }
                }
            ]
        }
    """.trimIndent()

    val MENU_ITEM_ADMIN = MenuItem.Admin(
        id = UUID.fromString("d72410fc-8361-4f14-9105-d96836f5533f"),
        icon = "cogs",
        text = "Admin Item",
        link = MenuLink(alias = "admin"),
        items = listOf(
            MenuItem.Admin(
                id = UUID.fromString("5c721ca4-7be1-4316-ae11-2a70800f18d8"),
                icon = "calendar",
                text = "Child Item",
                link = MenuLink(alias = "subitem")
            )
        )
    )
    val MENU_ITEM_ADMIN_JSON = """
        {
            "id": "d72410fc-8361-4f14-9105-d96836f5533f",
            "icon": "cogs",
            "text": "Admin Item",
            "link": {
                "alias": "admin"
            },
            "items": [
                {
                    "id": "5c721ca4-7be1-4316-ae11-2a70800f18d8",
                    "icon": "calendar",
                    "text": "Child Item",
                    "link": {
                        "alias": "subitem"
                    }
                }
            ]
        }
    """.trimIndent()
}
