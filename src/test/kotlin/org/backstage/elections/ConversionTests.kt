package org.backstage.elections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldContain
import org.backstage.error.HttpException
import javax.ws.rs.core.Response.Status

class ElectionConverterTests : BehaviorSpec() {
    init {
        Given("a valid request to create an election") {
            val createRequest = createRequest

            When("converting to an election entity") {
                val entity = ElectionConverter.createEntity(createRequest)

                Then("the attributes are all correctly mapped") {
                    entity.type shouldBe createRequest.type
                    entity.nominationsStart shouldBe createRequest.nominations.start
                    entity.nominationsEnd shouldBe createRequest.nominations.end
                    entity.votingStart shouldBe createRequest.voting.start
                    entity.votingEnd shouldBe createRequest.voting.end
                    entity.hustingsStart shouldBe createRequest.hustingsStart
                    entity.hustingsLocation shouldBe createRequest.hustingsLocation
                }

                and("all the positions should be correctly mapped") {
                    entity.positions shouldHaveSize 7
                }
            }
        }

        Given("a hydrated entity") {
            val election = entityHydrated

            When("converting the entity to a response DTO") {
                val response = ElectionConverter.toResponse(election)

                Then("all the attributes should be mapped") {
                    response.id shouldNot beNull()
                    response.id shouldBe election.id
                    response.type shouldBe election.type

                    response.nominations.start shouldBe election.nominationsStart
                    response.nominations.end shouldBe election.nominationsEnd

                    response.voting.start shouldBe election.votingStart
                    response.voting.end shouldBe election.votingEnd

                    response.hustingsStart shouldNot beNull()
                    response.hustingsStart shouldBe election.hustingsStart

                    response.hustingsLocation shouldNot beNull()
                    response.hustingsLocation shouldBe election.hustingsLocation

                    response.bathStudentId shouldNot beNull()
                    response.bathStudentId shouldBe election.bathStudentId

                    response.positions shouldHaveSize 7
                }
            }
        }
    }
}
