package org.backstage.util

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class EntityNameFormatTests : BehaviorSpec() {
    init {
        Given("a class of an entity with one word before Entity") {
            val entityClass = ExampleEntity::class

            When("determining the entity name") {
                val entityClassName = entityClass.formatClassName()

                Then("The class name should be correct") {
                    entityClassName shouldBe "example"
                }
            }
        }
        Given("the class of an entity with multiple words in it") {
            val entityClass = MultipleWordsInNameEntity::class

            When("determining the entity name") {
                val entityClassName = entityClass.formatClassName()

                Then("The class name should be correct") {
                    entityClassName shouldBe "multiple words in name"
                }
            }
        }
    }
}

data class ExampleEntity(var column: String) : BaseEntity()
data class MultipleWordsInNameEntity(var column: String) : BaseEntity()
