package org.backstage.error

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.backstage.error.JsonExceptionHandler.Companion.handleInvalidFormat
import org.backstage.error.JsonExceptionHandler.Companion.handleInvalidJsonType
import org.backstage.error.JsonExceptionHandler.Companion.handleMismatchedInput
import org.backstage.error.JsonExceptionHandler.Companion.handleValidationConstraintViolation
import org.backstage.error.PersistenceExceptionHandler.Companion.handleEntityNotFound
import org.backstage.error.PersistenceExceptionHandler.Companion.handleHibernateConstraintViolation
import org.backstage.http.HttpHeaders
import org.jboss.logging.Logger
import org.valiktor.ConstraintViolationException
import java.time.format.DateTimeParseException
import javax.persistence.EntityNotFoundException
import javax.ws.rs.NotAllowedException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.Response.StatusType
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider
import org.hibernate.exception.ConstraintViolationException as HibernateConstraintViolationException

private val logger = Logger.getLogger(ExceptionResponseFactory::class.java)

@Provider
class GlobalExceptionMapper : ExceptionMapper<Throwable> {
    override fun toResponse(exception: Throwable): Response = ExceptionResponseFactory.handleThrownException(exception)
}

interface ExceptionResponseFactory {
    companion object {
        fun handleThrownException(exception: Throwable?): Response {
            tailrec fun handleForException(exception: Throwable?, parent: Throwable? = null): Response =
                when (exception) {
                    is IllegalArgumentException -> handleIllegalArgumentException(exception)
                    is EntityNotFoundException -> handleEntityNotFound(exception)
                    is MissingKotlinParameterException -> handleMissingParameter(exception)
                    is HttpException -> handleHttpException(exception)
                    is InvalidFormatException -> handleInvalidFormat(exception)
                    is InvalidTypeIdException -> handleInvalidJsonType(exception)
                    is MismatchedInputException -> handleMismatchedInput(exception)
                    is HibernateConstraintViolationException -> handleHibernateConstraintViolation(exception)
                    is NotImplementedError -> handleNotImplemented(exception)
                    is NotAllowedException -> handleNotAllowed(exception)
                    is ConstraintViolationException -> handleValidationConstraintViolation(exception)
                    null, parent -> unhandledException(
                        parent ?: Exception("An unknown and unhandled error has occurred")
                    )
                    else -> handleForException(exception.cause, exception)
                }

            return handleForException(exception, null)
        }

        /**
         * As Kotlin is null-safe, we can't use the javax @NotNull annotation; instead Jackson will throw a
         * [MissingKotlinParameterException] when a non-nullable attribute isn't provided.
         */
        private fun handleMissingParameter(exception: MissingKotlinParameterException): Response =
            UnprocessableEntityStatus.buildValidationErrorResponse(
                field = exception.path.buildPath(),
                value = null,
                messageKey = ErrorCode.NOT_MISSING
            )

        private fun handleIllegalArgumentException(exception: Throwable): Response = exception
            .apply(logger::recordError)
            .buildGeneralErrorResponse(status = Status.BAD_REQUEST)

        private fun handleHttpException(exception: HttpException): Response = exception
            .apply(logger::recordError)
            .buildGeneralErrorResponse()

        private fun handleNotAllowed(exception: NotAllowedException): Response = exception.response

        private fun handleNotImplemented(
            @Suppress("UNUSED_PARAMETER") exception: NotImplementedError
        ): Response = Status.NOT_IMPLEMENTED
            .buildResponse(message = "Method not implemented")

        private fun unhandledException(exception: Throwable): Response = exception
            .apply(logger::recordError)
            .buildGeneralErrorResponse(status = Status.INTERNAL_SERVER_ERROR)
    }
}

interface JsonExceptionHandler {
    companion object {
        fun handleInvalidFormat(exception: InvalidFormatException): Response {
            val fieldPath = exception.path.buildPath()

            return when (val cause = exception.cause) {
                is DateTimeParseException -> handleDateTimeParseException(cause, fieldPath)
                else -> when (exception.targetType.isEnum) {
                    true -> handleInvalidEnumValue(exception, fieldPath)
                    false -> UnprocessableEntityStatus.buildValidationErrorResponse(
                        field = fieldPath,
                        value = exception.value,
                        messageKey = ErrorCode.INVALID_FORMAT
                    )
                }
            }
        }

        fun handleInvalidJsonType(exception: InvalidTypeIdException): Response =
            UnprocessableEntityStatus.buildValidationErrorResponse(
                field = exception.path.buildPath(),
                value = exception.typeId,
                messageKey = ErrorCode.INVALID_JSON_TYPE
            )

        fun handleMismatchedInput(exception: MismatchedInputException): Response =
            UnprocessableEntityStatus.buildValidationErrorResponse(
                field = exception.path.buildPath(),
                value = null, // TODO: I can't seem to get the current value out from anywhere, which makes me sad
                messageKey = ErrorCode.INCORRECT_TYPE,
                messageParams = mapOf(Pair("expectedType", exception.targetType.name.substringAfterLast(".")))
            )

        private fun handleInvalidEnumValue(exception: InvalidFormatException, fieldPath: String) =
            UnprocessableEntityStatus.buildValidationErrorResponse(
                field = fieldPath,
                value = exception.value,
                messageKey = ErrorCode.INVALID_ENUM_VALUE,
                messageParams = mapOf(Pair("allowedValue", exception.targetType.enumConstants))
            )

        fun handleValidationConstraintViolation(cause: ConstraintViolationException): Response =
            Response.status(UnprocessableEntityStatus)
                .entity(cause.constraintViolations)
                .build()

        private fun handleDateTimeParseException(exception: DateTimeParseException, fieldPath: String): Response =
            UnprocessableEntityStatus.buildValidationErrorResponse(
                field = fieldPath,
                value = exception.parsedString,
                messageKey = ErrorCode.INVALID_DATETIME_FORMAT
            )
    }
}

interface PersistenceExceptionHandler {
    companion object {
        fun handleHibernateConstraintViolation(exception: HibernateConstraintViolationException): Response = exception
            .apply(logger::recordWarning)
            .buildGeneralErrorResponse(UnprocessableEntityStatus)

        fun handleEntityNotFound(exception: EntityNotFoundException): Response = exception
            .apply(logger::recordError)
            .buildGeneralErrorResponse(Status.NOT_FOUND)
    }
}

fun Logger.recordError(exception: Throwable) = this.error(exception.message)
fun Logger.recordWarning(exception: Throwable) = this.warn(exception.message)

fun Throwable.buildGeneralErrorResponse(status: StatusType): Response =
    Response.status(status)
        .entity(GeneralError(code = status.statusCode, message = message))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .build()

fun HttpException.buildGeneralErrorResponse(): Response = this.buildGeneralErrorResponse(this.status)

fun StatusType.buildResponse(message: String? = null): Response =
    Response.status(this)
        .entity(GeneralError(code = statusCode, message = message))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .build()

fun UnprocessableEntityStatus.buildValidationErrorResponse(
    field: String,
    value: Any?,
    messageKey: String?,
    messageParams: Map<String, Any?> = emptyMap()
): Response {
    val constraint = when (messageKey) {
        null -> null
        else -> ValidationError.Constraint(
            name = messageKey,
            messageParams = messageParams.plus(Pair("value", value))
        )
    }

    val error = ValidationError(property = field, value = value, constraint = constraint)

    return Response.status(this.statusCode)
        .entity(listOf(error))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .build()
}

object UnprocessableEntityStatus : StatusType {
    override fun getStatusCode(): Int = 422
    override fun getReasonPhrase(): String = "Unprocessable entity"
    override fun getFamily(): Status.Family = Status.Family.CLIENT_ERROR
}

fun List<JsonMappingException.Reference>.buildPath(): String =
    this.joinToString(separator = "") { pathPart ->
        when (pathPart.fieldName == null) {
            true -> "[${pathPart.index}]"
            false -> ".${pathPart.fieldName}"
        }
    }.removePrefix(".")
