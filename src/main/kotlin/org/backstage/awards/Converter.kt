package org.backstage.awards

import io.quarkus.security.identity.SecurityIdentity
import org.backstage.auth.getUserId

object AwardConverter {
    fun toEntity(request: AwardRequest.Create, identity: SecurityIdentity) = AwardEntity(
        name = request.name,
        description = request.description,
        recurring = request.recurring,
        suggestedBy = identity.getUserId(),
        approved = false
    )

    inline fun <reified T> toResponse(entity: AwardEntity): T = when (T::class) {
        AwardResponse.Full::class -> AwardResponse.Full(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            recurring = entity.recurring,
            suggestedBy = entity.suggestedBy,
            approved = entity.approved
        ) as T
        else -> throw IllegalArgumentException("Cannot convert ${AwardEntity::class} to ${T::class}")
    }
}
