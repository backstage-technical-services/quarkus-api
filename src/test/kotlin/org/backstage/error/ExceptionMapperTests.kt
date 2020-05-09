package org.backstage.error

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import org.backstage.http.HttpHeaders
import java.sql.SQLException
import javax.persistence.EntityNotFoundException
import javax.ws.rs.NotAllowedException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.*
import javax.ws.rs.core.Response.StatusType
import kotlin.reflect.KClassifier
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import org.hibernate.exception.ConstraintViolationException as HibernateConstraintViolationException

class ExceptionHandlerTests : BehaviorSpec() {
    init {
        Given("a missing parameter exception") {
            val exception = MissingKotlinParameterException(
                parameter = object : KParameter {
                    override val annotations: List<Annotation>
                        get() = emptyList()
                    override val index: Int
                        get() = 1
                    override val isOptional: Boolean
                        get() = false
                    override val isVararg: Boolean
                        get() = false
                    override val kind: KParameter.Kind
                        get() = KParameter.Kind.VALUE
                    override val name: String?
                        get() = "name"
                    override val type: KType
                        get() = object : KType {
                            override val annotations: List<Annotation>
                                get() = emptyList()
                            override val arguments: List<KTypeProjection>
                                get() = emptyList()
                            override val classifier: KClassifier?
                                get() = null
                            override val isMarkedNullable: Boolean
                                get() = false

                        }
                },
                processor = null,
                msg = "Message"
            )

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the status code should be correct") {
                    response.status shouldBe UnprocessableEntityStatus.statusCode
                }
                Then("the response should have the content type") {
                    response.shouldHaveContentTypeHeader()
                }
                Then("the body should have the correct information") {
                    response.shouldBeValidationErrorWith(
                        field = "",
                        value = null,
                        messageKey = ErrorCode.NOT_MISSING,
                        messageParams = mapOf("value" to null)
                    )
                }
            }
        }

        Given("an illegal argument exception") {
            val exception = IllegalArgumentException()

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the status code should be correct") {
                    response.status shouldBe BAD_REQUEST.statusCode
                }
                Then("the response should have the content type") {
                    response.shouldHaveContentTypeHeader()
                }
                Then("The response should have the correct message") {
                    response.shouldBeGeneralErrorWith(
                        status = BAD_REQUEST,
                        message = null
                    )
                }
            }
        }

        Given("a HttpException") {
            val exception = HttpException(BAD_GATEWAY, "Message")

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the response should match the extension function") {
                    with(exception.buildGeneralErrorResponse()) {
                        response.status shouldBe this.status
                        response.headers shouldBe this.headers
                    }
                }
            }
        }

        Given("a not allowed exception") {
            val exception = NotAllowedException(Throwable(), "GET")

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the status code should be correct") {
                    response.status shouldBe METHOD_NOT_ALLOWED.statusCode
                }
            }
        }

        Given("a not implemented error") {
            val exception = NotImplementedError()

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the status code should be correct") {
                    response.status shouldBe NOT_IMPLEMENTED.statusCode
                }
                Then("the response should have the content type") {
                    response.shouldHaveContentTypeHeader()
                }
                Then("The response should have the correct message") {
                    response.shouldBeGeneralErrorWith(
                        status = NOT_IMPLEMENTED,
                        message = "Method not implemented"
                    )
                }
            }
        }

        Given("a general Throwable") {
            val exception = Throwable("Message")

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the status code should be correct") {
                    response.status shouldBe INTERNAL_SERVER_ERROR.statusCode
                }
                Then("the response should have the content type") {
                    response.shouldHaveContentTypeHeader()
                }
            }
        }

        Given("no exception") {
            val exception: Throwable? = null

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the status code should be correct") {
                    response.status shouldBe INTERNAL_SERVER_ERROR.statusCode
                }
                Then("the response should have the content type") {
                    response.shouldHaveContentTypeHeader()
                }
                Then("the body should have the expected message") {
                    response.shouldBeGeneralErrorWith(
                        status = INTERNAL_SERVER_ERROR,
                        message = "An unknown and unhandled error has occurred"
                    )
                }
            }
        }
    }
}

class PersistenceExceptionHandlerTests : BehaviorSpec() {
    init {
        Given("a hibernate constraint violation exception") {
            val exception = HibernateConstraintViolationException("Message", SQLException(), "Constraint")

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the response should have the correct status code") {
                    response.status shouldBe UnprocessableEntityStatus.statusCode
                }
                Then("the response should have the content type") {
                    response.shouldHaveContentTypeHeader()
                }
                Then("the response should have the correct body") {
                    response.shouldBeGeneralErrorWith(
                        status = UnprocessableEntityStatus,
                        message = exception.message
                    )
                }
            }
        }

        Given("an entity not found exception") {
            val exception = EntityNotFoundException()

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the response should have the correct status code") {
                    response.status shouldBe NOT_FOUND.statusCode
                }
                Then("the response should have the content type") {
                    response.shouldHaveContentTypeHeader()
                }
                Then("the response should have the correct body") {
                    response.shouldBeGeneralErrorWith(
                        status = NOT_FOUND,
                        message = null
                    )
                }
            }
        }
    }
}

class BuildErrorResponseTests : DescribeSpec() {
    init {
        describe("building a validation error response from an unprocessable entity status") {
            val response = UnprocessableEntityStatus.buildValidationErrorResponse(
                field = "FIELD",
                value = "VALUE",
                messageKey = "KEY",
                messageParams = mapOf("value" to "VALUE")
            )

            it("the response should contain the correct status code") {
                response.status shouldBe UnprocessableEntityStatus.statusCode
            }
            it("the response should include the content type header") {
                response.shouldHaveContentTypeHeader()
            }
            it("the body should be correct") {
                response.shouldBeValidationErrorWith(
                    field = "FIELD",
                    value = "VALUE",
                    messageKey = "KEY",
                    messageParams = mapOf("value" to "VALUE")
                )
            }
        }

        describe("building a validation error response from an unprocessable entity status with no message key") {
            val response = UnprocessableEntityStatus.buildValidationErrorResponse(
                field = "FIELD",
                value = "VALUE",
                messageKey = null
            )

            it("the constraint should be null") {
                response.shouldBeValidationErrorWith(
                    field = "FIELD",
                    value = "VALUE",
                    messageKey = null,
                    messageParams = emptyMap()
                )
            }
        }

        describe("building an error response from any status") {
            val status = NOT_FOUND
            val response = status.buildResponse(
                message = "The error message"
            )

            it("the response should have the correct status code") {
                response.status shouldBe status.statusCode
            }
            it("the response should include the content type header") {
                response.shouldHaveContentTypeHeader()
            }
            it("the body should contain the correct information") {
                response.shouldBeGeneralErrorWith(
                    status = status,
                    message = "The error message"
                )
            }
        }

        describe("building an error response from a status with no message") {
            val response = NOT_FOUND.buildResponse()

            it("the message should be null") {
                response.entity.shouldBeTypeOf<GeneralError> { error ->
                    error.message.shouldBeNull()
                }
            }
        }

        describe("building an error response from a general Throwable and status code") {
            val status = NOT_FOUND
            val exception = Throwable("A test exception")
            val response = exception.buildGeneralErrorResponse(status)

            it("the response should have the correct status code") {
                response.status shouldBe status.statusCode
            }
            it("the response should include the content type header") {
                response.shouldHaveContentTypeHeader()
            }
            it("the body should contain the correct information") {
                response.shouldBeGeneralErrorWith(
                    status = status,
                    message = exception.message
                )
            }
        }

        describe("building an error response from a HttpException") {
            val exception = HttpException(BAD_REQUEST, "Bad Request")
            val response = exception.buildGeneralErrorResponse()

            it("the response should have the correct status code") {
                response.status shouldBe exception.status.statusCode
            }
            it("the response should include the content type header") {
                response.shouldHaveContentTypeHeader()
            }
            it("the body should contain the correct information") {
                response.shouldBeGeneralErrorWith(
                    status = exception.status,
                    message = exception.message
                )
            }
        }
    }
}

class PathBuildingTests : BehaviorSpec() {
    init {
        Given("a list of paths, with and without field names") {
            val paths = listOf(
                JsonMappingException.Reference("from", "field"),
                JsonMappingException.Reference("from", 1),
                JsonMappingException.Reference("from", "second"),
                JsonMappingException.Reference("from", "third"),
                JsonMappingException.Reference("from", 6)
            )

            When("building to a string") {
                val path = paths.buildPath()

                Then("the result should be correct") {
                    path shouldBe "field[1].second.third[6]"
                }
            }
        }
    }
}

fun Response.shouldHaveContentTypeHeader(headerValue: String = MediaType.APPLICATION_JSON) {
    this.headers[HttpHeaders.CONTENT_TYPE]?.firstOrNull() shouldBe headerValue
}

fun Response.shouldBeValidationErrorWith(
    field: String,
    value: Any? = null,
    messageKey: String? = null,
    messageParams: Map<String, Any?> = emptyMap()
) {
    (this.entity as List<*>).let { errors ->
        errors shouldHaveSize 1

        errors.first().shouldBeTypeOf<ValidationError> { error ->
            error.property shouldBe field

            when (value) {
                null -> error.value.shouldBeNull()
                else -> {
                    error.value.shouldNotBeNull()
                    error.value shouldBe value
                }
            }

            when (messageKey) {
                null -> error.constraint.shouldBeNull()
                else -> {
                    error.constraint.shouldNotBeNull()

                    error.constraint?.messageKey?.shouldBe("org.backstage.constraints.$messageKey.message")
                    error.constraint?.messageBundle?.shouldBe("org/backstage/messages")
                    error.constraint?.messageParams?.shouldContainAll(messageParams)
                }
            }
        }
    }
}

fun Response.shouldBeGeneralErrorWith(status: StatusType, message: String?) {
    this.entity.shouldBeTypeOf<GeneralError> { error ->
        error.code shouldBe status.statusCode

        when (message) {
            null -> error.message.shouldBeNull()
            else -> {
                error.message.shouldNotBeNull()
                error.message shouldBe message
            }
        }

        error.timestamp.shouldNotBeNull()
    }
}
