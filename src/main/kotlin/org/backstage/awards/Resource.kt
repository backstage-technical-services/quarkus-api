package org.backstage.awards

import org.backstage.http.HttpHeaders
import org.backstage.util.SimpleCrudResource
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/award")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class AwardResource(
    private val service: AwardService
) : SimpleCrudResource<AwardRequest.Create, AwardRequest.Update> {
    @GET
    override fun list(): Response = service.list(AwardResponse.Full::class)
        .let { awards ->
            Response.ok(awards)
                .build()
        }

    @POST
    override fun create(
        request: AwardRequest.Create
    ): Response = service.create(request)
        .let { awardId ->
            Response.noContent()
                .header(HttpHeaders.RESOURCE_ID, awardId)
                .build()
        }

    @GET
    @Path("/{id}")
    override fun get(
        @PathParam("id") id: UUID
    ): Response = service.get(id, AwardResponse.Full::class)
        .let { award ->
            Response.ok(award)
                .build()
        }

    @PATCH
    @Path("/{id}")
    override fun update(
        @PathParam("id") id: UUID,
        request: AwardRequest.Update
    ): Response {
        service.update(id, request)

        return Response.noContent()
            .build()
    }

    @PATCH
    @Path("/{id}/approve")
    fun approve(
        @PathParam("id") id: UUID
    ): Response {
        service.approve(id)

        return Response.noContent()
            .build()
    }

    @PATCH
    @Path("/{id}/unapprove")
    fun unapprove(
        @PathParam("id") id: UUID
    ): Response {
        service.unapprove(id)

        return Response.noContent()
            .build()
    }

    @DELETE
    @Path("/{id}")
    override fun delete(
        @PathParam("id") id: UUID
    ): Response {
        service.delete(id)

        return Response.noContent()
            .build()
    }
}
