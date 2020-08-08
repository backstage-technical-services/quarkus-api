package org.backstage.menu

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.assertions.json.shouldNotContainJsonKey
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import org.backstage.util.objectMapper
import org.valiktor.ConstraintViolationException

class MenuLinkValidationTests : FunSpec() {
    init {
        test("instantiating with a href should be fine") {
            shouldNotThrow<ConstraintViolationException> {
                MenuLink(href = "href")
            }
        }
        test("instantiating with an alias should be fine") {
            shouldNotThrow<ConstraintViolationException> {
                MenuLink(alias = "alias")
            }
        }
        test("instantiating with no parameters should throw an exception") {
            shouldThrow<ConstraintViolationException> {
                MenuLink()
            }
        }
    }
}

class MainLinkSerialisationTests : BehaviorSpec() {
    init {
        Given("a menu link with a href") {
            val menuLink = MenuFixtures.MENU_LINK_HREF

            When("serialising to JSON") {
                val menuLinkJson = objectMapper.writeValueAsString(menuLink)

                Then("the DTO should be serialised correctly") {
                    menuLinkJson.shouldMatchJson(MenuFixtures.MENU_LINK_HREF_JSON)
                }
            }
        }
        Given("a menu link with an alias") {
            val menuLink = MenuFixtures.MENU_LINK_ALIAS

            When("serialising to JSON") {
                val menuLinkJson = objectMapper.writeValueAsString(menuLink)

                Then("the DTO should be serialised correctly") {
                    menuLinkJson.shouldMatchJson(MenuFixtures.MENU_LINK_ALIAS_JSON)
                }
            }
        }
    }
}

class MainMenuSerialisationTests : BehaviorSpec() {
    init {
        Given("a main menu item with children") {
            val menuItem = MenuFixtures.MENU_ITEM_MAIN

            When("serialising to JSON") {
                val menuItemJson = objectMapper.writeValueAsString(menuItem)

                Then("the DTO should be serialised correctly") {
                    menuItemJson.shouldMatchJson(MenuFixtures.MENU_ITEM_MAIN_JSON)
                }
            }
        }
        Given("a main menu item with no children") {
            val menuItem = MenuFixtures.MENU_ITEM_MAIN.copy(children = null)

            When("serialising to JSON") {
                val menuItemJson = objectMapper.writeValueAsString(menuItem)

                Then("the children key should not be present") {
                    menuItemJson.shouldNotContainJsonKey("children")
                }
            }
        }
        Given("a main menu with multiple items") {
            val menu = listOf(MenuFixtures.MENU_ITEM_MAIN, MenuFixtures.MENU_ITEM_MAIN)

            When("serialising to JSON") {
                val menuJson = objectMapper.writeValueAsString(menu)

                Then("the DTOs should be serialised correctly") {
                    menuJson.shouldMatchJson(
                        """
                        [
                        ${MenuFixtures.MENU_ITEM_MAIN_JSON},
                        ${MenuFixtures.MENU_ITEM_MAIN_JSON}
                        ]
                    """.trimIndent()
                    )
                }
            }
        }
    }
}

class AdminMenuSerialisationTests : BehaviorSpec() {
    init {
        Given("an admin menu item with items") {
            val menuItem = MenuFixtures.MENU_ITEM_ADMIN

            When("serialising to JSON") {
                val menuItemJson = objectMapper.writeValueAsString(menuItem)

                Then("the DTO should be serialised correctly") {
                    menuItemJson.shouldMatchJson(MenuFixtures.MENU_ITEM_ADMIN_JSON)
                }
            }
        }
        Given("an admin menu item with no children") {
            val menuItem = MenuFixtures.MENU_ITEM_ADMIN.copy(items = null)

            When("serialising to JSON") {
                val menuItemJson = objectMapper.writeValueAsString(menuItem)

                Then("the items key should not be present") {
                    menuItemJson.shouldNotContainJsonKey("items")
                }
            }
        }
        Given("an admin menu with multiple items") {
            val menu = listOf(MenuFixtures.MENU_ITEM_ADMIN, MenuFixtures.MENU_ITEM_ADMIN)

            When("serialising to JSON") {
                val menuJson = objectMapper.writeValueAsString(menu)

                Then("the DTOs should be serialised correctly") {
                    menuJson.shouldMatchJson(
                        """
                        [
                        ${MenuFixtures.MENU_ITEM_ADMIN_JSON},
                        ${MenuFixtures.MENU_ITEM_ADMIN_JSON}
                        ]
                    """.trimIndent()
                    )
                }
            }
        }
    }
}
