package org.backstage.awards

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.backstage.util.objectMapper

class AwardSerialisationTests : BehaviorSpec() {
    init {
        Given("the JSON for a create request") {
            val requestJson = AwardFixtures.CREATE_REQUEST_JSON

            When("de-serialising the request") {
                val request = objectMapper.readValue<AwardRequest.Create>(requestJson)

                Then("the DTO should be de-serialised correctly") {
                    request shouldBe AwardFixtures.CREATE_REQUEST
                }
            }
        }

        Given("the JSON for an update request") {
            val requestJson = AwardFixtures.UPDATE_REQUEST_JSON

            When("de-serialising the request") {
                val request = objectMapper.readValue<AwardRequest.Update>(requestJson)

                Then("the DTO should be de-serialised correctly") {
                    request shouldBe AwardFixtures.UPDATE_REQUEST
                }
            }
        }

        Given("the full response DTO") {
            val response = AwardFixtures.RESPONSE_FULL.copy()

            When("serialising the response") {
                val responseJson = objectMapper.writeValueAsString(response)

                Then("the response should be serialised correctly") {
                    responseJson.shouldMatchJson(AwardFixtures.RESPONSE_FULL_JSON)
                }
            }
        }
    }
}
