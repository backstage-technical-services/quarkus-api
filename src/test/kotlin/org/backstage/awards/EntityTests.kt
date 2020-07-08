package org.backstage.awards

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class EntityTests : BehaviorSpec() {
    init {
        Given("an existing entity") {
            val entity = AwardFixtures.HYDRATED_ENTITY

            When("determining the author ID") {
                val authorId = entity.authorId

                Then("the author ID should be the same as suggestedBy") {
                    authorId shouldBe entity.suggestedBy
                }
            }
        }
    }
}
