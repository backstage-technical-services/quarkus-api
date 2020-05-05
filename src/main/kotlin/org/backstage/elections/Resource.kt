package org.backstage.elections

import org.backstage.http.HttpHeaders
import org.backstage.util.SimpleCrudResource
import java.net.URI
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/election")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class ElectionResource(
    private val service: ElectionService
) : SimpleCrudResource<CreateElection, UpdateElection> {
    @GET
    override fun list(): Response = service
        .list(ElectionResponse::class)
        .run {
            Response.ok()
                .entity(this)
                .build()
        }

    @POST
    override fun create(request: CreateElection): Response = service
        .create(request)
        .run {
            Response.created(URI("/election/$this"))
                .header(HttpHeaders.RESOURCE_ID, this)
                .build()
        }

    @GET
    @Path("/{id}")
    override fun get(@PathParam("id") id: Long): Response = service
        .get(id, ElectionResponse::class)
        .run {
            Response.ok()
                .entity(this)
                .build()
        }

    @PUT
    @Path("/{id}")
    override fun update(
        @PathParam("id") id: Long,
        request: UpdateElection
    ): Response = service
        .update(id, request)
        .run {
            Response.noContent()
                .build()
        }

    @DELETE
    @Path("/{id}")
    override fun delete(@PathParam("id") id: Long): Response = service
        .delete(id)
        .run {
            Response.noContent()
                .build()
        }
}
