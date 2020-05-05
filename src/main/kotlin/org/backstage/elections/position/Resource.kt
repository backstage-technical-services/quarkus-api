package org.backstage.elections.position

import org.backstage.elections.ElectionEntity
import org.backstage.elections.ElectionService
import org.backstage.http.HttpHeaders
import java.net.URI
import java.util.*
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/election/{electionId}/position")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class ElectionPositionResource @Inject constructor(
    private val electionService: ElectionService,
    private val positionService: ElectionPositionService
) {
    @POST
    fun create(
        @PathParam("electionId") electionId: Long,
        request: CreateElectionPosition
    ): Response = getElection(electionId)
        .let { election -> positionService.create(election, request) }
        .let { positionId ->
            Response.created(URI("/election/$electionId/position/$positionId"))
                .header(HttpHeaders.RESOURCE_ID, positionId)
                .build()
        }

    @PUT
    @Path("/{positionId}")
    fun update(
        @PathParam("electionId") electionId: Long,
        @PathParam("positionId") positionId: Long,
        request: UpdateElectionPosition
    ): Response = getElection(electionId)
        .let { election -> positionService.update(election, positionId, request) }
        .run {
            Response.noContent()
                .build()
        }

    @DELETE
    @Path("/{positionId}")
    fun delete(
        @PathParam("electionId") electionId: Long,
        @PathParam("positionId") positionId: Long
    ): Response = getElection(electionId)
        .let { election -> positionService.delete(election, positionId) }
        .run {
            Response.noContent()
                .build()
        }

    private fun getElection(electionId: Long) = electionService.get(electionId, ElectionEntity::class)
}
