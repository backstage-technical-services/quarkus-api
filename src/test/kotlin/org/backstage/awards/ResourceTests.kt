package org.backstage.awards

import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.mockito.InjectMock
import io.restassured.RestAssured
import org.backstage.*
import org.backstage.auth.Roles.ROLE_MEMBER
import org.backstage.auth.Roles.ROLE_SUPER_ADMIN
import org.backstage.http.HttpHeaders
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockito.Mockito
import javax.ws.rs.core.Response.Status.NO_CONTENT
import javax.ws.rs.core.Response.Status.OK

@QuarkusTest
@TestInstance(PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class ResourceTests {
    @InjectMock
    private lateinit var identity: SecurityIdentity

    private lateinit var awardId: String

    @BeforeEach
    fun setup() {
        Mockito.`when`(identity.hasRole(ROLE_MEMBER)).thenReturn(true)
        Mockito.`when`(identity.hasRole(ROLE_SUPER_ADMIN)).thenReturn(true)
        Mockito.`when`(identity.principal).thenAnswer { AuthHelpers.createMockedPrincipal() }
    }

    @Test
    fun `listing the awards should return a valid response`() {
        RestAssured
            .get("/award")

            .then()
            .isJson()
            .statusCode(OK)
            .body("size()", greaterThan(0))
    }

    @Test
    @Order(1)
    fun `creating an award should return a 204 status code and the resource ID`() {
        RestAssured
            .given()
            .isJson(AwardFixtures.CREATE_REQUEST_JSON)

            .`when`()
            .post("/award")

            .then()
            .statusCode(NO_CONTENT)
            .header(HttpHeaders.RESOURCE_ID, notNullValue())

            .and()
            .extract()
            .header(HttpHeaders.RESOURCE_ID)
            .also { awardId -> this.awardId = awardId }
    }

    @Test
    @Order(2)
    fun `getting the details of an award should return the award details`() {
        RestAssured
            .get("/award/$awardId")

            .then()
            .isJson()
            .statusCode(OK)
            .body("id", equalTo(awardId))
    }

    @Test
    fun `getting the details of a non-existent award should return a 404 response`() {
        RestAssured
            .get("/award/${AwardFixtures.NON_EXISTENT_ID}")

            .then()
            .shouldShowNotFound()
    }

    @Test
    fun `getting the details of an award using an invalid ID should return a 400 response`() {
        RestAssured
            .get("/award/INVALID")

            .then()
            .shouldShowInvalidUUID()
    }

    @Test
    @Order(3)
    fun `updating an existing award should return a 204 status code`() {
        RestAssured
            .given()
            .isJson(AwardFixtures.UPDATE_REQUEST_JSON)

            .`when`()
            .patch("/award/$awardId")

            .then()
            .statusCode(NO_CONTENT)
    }

    @Test
    fun `updating a non-existent award should return a 404 response`() {
        RestAssured
            .given()
            .isJson(AwardFixtures.UPDATE_REQUEST_JSON)

            .`when`()
            .patch("/award/${AwardFixtures.NON_EXISTENT_ID}")

            .then()
            .shouldShowNotFound()
    }

    @Test
    fun `updating the details of an award using an invalid UUID should a 400 response`() {
        RestAssured
            .given()
            .isJson(AwardFixtures.UPDATE_REQUEST_JSON)

            .`when`()
            .patch("/award/INVALID")

            .then()
            .shouldShowInvalidUUID()
    }

    @Test
    @Order(4)
    fun `approving an existing award should return a 204 status code`() {
        RestAssured
            .patch("/award/$awardId/approve")

            .then()
            .statusCode(NO_CONTENT)
    }

    @Test
    fun `approving a non-existent award should return a 404 response`() {
        RestAssured
            .patch("/award/${AwardFixtures.NON_EXISTENT_ID}/approve")

            .then()
            .shouldShowNotFound()
    }

    @Test
    fun `approving an award with an invalid UUID should return a 400 response`() {
        RestAssured
            .patch("/award/INVALID/approve")

            .then()
            .shouldShowInvalidUUID()
    }

    @Test
    @Order(5)
    fun `unapproving an existing award should return a 204 status code`() {
        RestAssured
            .patch("/award/$awardId/unapprove")

            .then()
            .statusCode(NO_CONTENT)
    }

    @Test
    fun `unapproving a non-existent award should return a 404 response`() {
        RestAssured
            .patch("/award/${AwardFixtures.NON_EXISTENT_ID}/unapprove")

            .then()
            .shouldShowNotFound()
    }

    @Test
    fun `unapproving an award with an invalid UUID should return a 400 response`() {
        RestAssured
            .patch("/award/INVALID/unapprove")

            .then()
            .shouldShowInvalidUUID()
    }

    @Test
    @Order(6)
    fun `deleting an existing award should return a 204 status code`() {
        RestAssured
            .delete("/award/$awardId")

            .then()
            .statusCode(NO_CONTENT)
    }

    @Test
    fun `deleting a non-existent award should return the 404 response`() {
        RestAssured
            .delete("/award/${AwardFixtures.NON_EXISTENT_ID}")

            .then()
            .shouldShowNotFound()
    }

    @Test
    fun `deleting an award with an invalid UUID should return a 400 response`() {
        RestAssured
            .delete("/award/INVALID")

            .then()
            .shouldShowInvalidUUID()
    }
}
