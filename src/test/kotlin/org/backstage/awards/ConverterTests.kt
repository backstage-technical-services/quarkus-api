package org.backstage.awards

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import org.backstage.AuthHelpers

class ConverterTests : BehaviorSpec() {
    init {
        val user = AuthHelpers.createMockedIdentity()

        Given("a request to create an award") {
            val request = AwardFixtures.CREATE_REQUEST.copy()

            When("converting to an entity") {
                val entity = AwardConverter.toEntity(request, user)

                Then("the attributes should be copied over") {
                    entity.name shouldBe request.name
                    entity.description shouldBe request.description
                    entity.recurring shouldBe request.recurring
                }
                Then("the award should not be approved") {
                    entity.approved.shouldBeFalse()
                }
                Then("the suggested by attribute should be set correctly") {
                    entity.suggestedBy shouldBe AuthHelpers.DEFAULT_USER_ID
                }
            }
        }

        Given("a hydrated entity") {
            val entity = AwardFixtures.HYDRATED_ENTITY

            When("converting to a full response DTO") {
                val response = AwardConverter.toResponse<AwardResponse.Full>(entity)

                Then("all the attributes should be set correctly") {
                    response.id shouldBe entity.id
                    response.name shouldBe entity.name
                    response.description shouldBe entity.description
                    response.recurring shouldBe entity.recurring
                    response.suggestedBy shouldBe entity.suggestedBy
                    response.approved shouldBe entity.approved
                }
            }

            When("converting to an unrecognised class") {
                val exception = shouldThrow<IllegalArgumentException> {
                    AwardConverter.toResponse<String>(entity)
                }

                Then("the exception should contain the correct text") {
                    exception.message shouldStartWith "Cannot convert"
                    exception.message shouldContain AwardEntity::class.toString()
                    exception.message shouldContain String::class.toString()
                }
            }
        }
    }
}
