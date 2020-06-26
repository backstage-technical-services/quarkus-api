package org.backstage

import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.Response.Status.*
import javax.ws.rs.core.Response.StatusType

fun RequestSpecification.isJson(): RequestSpecification = this.contentType(ContentType.JSON)
fun RequestSpecification.isJson(body: Any): RequestSpecification = this.isJson().body(body)

fun Response.`do`(block: Response.() -> Unit): Response = this.apply { block() }
fun Response.printResponse(): Response = this.apply { prettyPrint() }

fun ValidatableResponse.isJson(): ValidatableResponse = this.contentType(ContentType.JSON)
fun ValidatableResponse.statusCode(status: Status): ValidatableResponse = this.statusCode(status.statusCode)
fun ValidatableResponse.statusCode(status: StatusType): ValidatableResponse = this.statusCode(status.statusCode)

fun ValidatableResponse.shouldShowNotFound(): ValidatableResponse = this
    .isJson()
    .statusCode(NOT_FOUND)
    .body("code", Matchers.equalTo(NOT_FOUND.statusCode))
    .body("message", Matchers.containsString("Could not find"))

fun ValidatableResponse.shouldShowNotImplemented(): ValidatableResponse = this
    .isJson()
    .statusCode(NOT_IMPLEMENTED)
    .body("message", Matchers.containsString("Method not implemented"))

fun ValidatableResponse.shouldShowInvalidUUID(): ValidatableResponse = this
    .isJson()
    .statusCode(BAD_REQUEST)
    .and().body("message", Matchers.startsWith("Invalid UUID"))
