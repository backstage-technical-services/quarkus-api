package org.backstage.error

import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR

class HttpException(val status: Response.Status, message: String) : RuntimeException(message)

infix fun Response.Status.exceptionWithMessage(message: String) = HttpException(this, message)

interface ExceptionFactory {
    companion object {
        fun couldNotAssignId(entity: Any? = null) = when (entity) {
            null -> INTERNAL_SERVER_ERROR exceptionWithMessage "Failed to assign ID to entity"
            else -> INTERNAL_SERVER_ERROR exceptionWithMessage "Failed to assign ID to ${entity::class}"
        }

        fun entityMissingId(entity: Any) =
            INTERNAL_SERVER_ERROR exceptionWithMessage "Entity ${entity::class} is missing its ID"
    }
}
