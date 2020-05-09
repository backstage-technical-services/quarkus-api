package org.backstage.health

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

@QuarkusTest
class LivenessTest {
    @Test
    fun `liveness check should return as healthy`() {
        given()
            .`when`().get("/health/live")

            .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .and().body("checks[0].name", equalTo("APP"))
            .and().body("checks[0].status", equalTo("UP"))
    }
}
