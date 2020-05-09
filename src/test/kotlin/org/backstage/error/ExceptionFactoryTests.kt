package org.backstage.error

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith
import javax.ws.rs.core.Response.Status

class ExceptionFactoryTests : BehaviorSpec() {
    init {
        Given("a status code and message to create an exception with") {
            val status = Status.METHOD_NOT_ALLOWED
            val message = "This is a test message"

            When("creating an exception") {
                val exception = status exceptionWithMessage message

                Then("the status and message should be set") {
                    exception.status shouldBe status
                    exception.message shouldBe message
                }
            }
        }

        Given("an example entity") {
            val entity = TestEntity()

            When("creating an exception for no assigned ID") {
                val exception = ExceptionFactory.couldNotAssignId(entity)

                Then("the correct status code should be set") {
                    exception.status shouldBe Status.INTERNAL_SERVER_ERROR
                }
                Then("the entity's class should be in the message") {
                    exception.message shouldContain entity::class.toString()
                }
                Then("the message should contain enough detail") {
                    exception.message shouldStartWith "Failed to assign ID"
                }
            }

            When("creating an exception for an entity missing its ID") {
                val exception = ExceptionFactory.entityMissingId(entity)

                Then("the correct status code should be set") {
                    exception.status shouldBe Status.INTERNAL_SERVER_ERROR
                }
                Then("the entity's class should be in the message") {
                    exception.message shouldContain entity::class.toString()
                }
                Then("the message should contain enough detail") {
                    exception.message shouldEndWith "missing its ID"
                }
            }
        }
    }
}

class TestEntity
