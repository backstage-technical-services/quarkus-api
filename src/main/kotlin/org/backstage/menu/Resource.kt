package org.backstage.menu

import javax.annotation.security.RolesAllowed
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/menu")
@Produces(MediaType.APPLICATION_JSON)
class MenuResource(
    private val service: MenuService
) {
    @GET
    @Path("/main")
    fun getMainMenu(): Response = service.getMain()
        .let { menu ->
            Response.ok()
                .entity(menu)
                .build()
        }

    @GET
    @Path("/admin")
    @RolesAllowed("ROLE_COMMITTEE", "ROLE_SUPER_ADMIN")
    fun getAdminMenu(): Response = service.getAdmin()
        .let { menu ->
            Response.ok()
                .entity(menu)
                .build()
        }
}
