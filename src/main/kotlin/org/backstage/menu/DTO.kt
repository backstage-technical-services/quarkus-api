package org.backstage.menu

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.annotation.JsonProperty
import org.valiktor.functions.isNotNull
import org.valiktor.validate
import java.util.*

@JsonInclude(NON_NULL)
data class MenuLink(
    @JsonProperty("href")
    val href: String? = null,

    @JsonProperty("alias")
    val alias: String? = null
) {
    init {
        validate(this) { menuLink ->
            if (menuLink.alias == null) {
                validate(MenuLink::href)
                    .isNotNull()
            }
        }
    }
}

sealed class MenuItem {
    @JsonInclude(NON_NULL)
    data class Main(
        @JsonProperty("id")
        val id: UUID,

        @JsonProperty("text")
        val text: String,

        @JsonProperty("link")
        val link: MenuLink,

        @JsonProperty("children")
        val children: List<Main>? = null
    ) : MenuItem()

    @JsonInclude(NON_NULL)
    data class Admin(
        @JsonProperty("id")
        val id: UUID,

        @JsonProperty("icon")
        val icon: String,

        @JsonProperty("text")
        val text: String,

        @JsonProperty("link")
        val link: MenuLink,

        @JsonProperty("items")
        val items: List<Admin>? = null
    ) : MenuItem()
}

typealias MainMenu = List<MenuItem.Main>
typealias AdminMenu = List<MenuItem.Admin>
