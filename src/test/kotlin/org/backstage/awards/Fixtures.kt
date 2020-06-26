package org.backstage.awards

import org.backstage.AuthHelpers
import java.util.*

interface AwardFixtures {
    companion object {
        val CREATE_REQUEST = AwardRequest.Create(
            name = "New Award",
            description = "The description",
            recurring = true
        )
        val CREATE_REQUEST_JSON = """
            {
                "name": "New Award",
                "description": "The description",
                "recurring": true
            }
        """.trimIndent()

        val UPDATE_REQUEST = AwardRequest.Update(
            name = "Update Award",
            description = "The new description",
            recurring = false
        )
        val UPDATE_REQUEST_JSON = """
            {
                "name": "Update Award",
                "description": "The new description",
                "recurring": false
            }
        """.trimIndent()

        val EXISTING_ENTITY_ID: UUID = UUID.fromString("7268e219-9585-4b0c-b022-34f134701994")
        val NON_EXISTENT_ID: UUID = UUID.fromString("9b0e070d-0c99-400b-b75b-a5358d321415")
        val ENTITY = AwardEntity(
            name = "Unpersisted Award",
            description = "The description",
            recurring = false,
            suggestedBy = AuthHelpers.DEFAULT_USER_ID,
            approved = false
        )
        val HYDRATED_ENTITY = AwardEntity(
            name = "Existing Award",
            description = "The existing award",
            recurring = true,
            suggestedBy = AuthHelpers.DEFAULT_USER_ID,
            approved = true
        ).apply {
            id = EXISTING_ENTITY_ID
        }

        val RESPONSE_FULL = AwardResponse.Full(
            id = EXISTING_ENTITY_ID,
            name = "Response Award",
            description = "The response description",
            recurring = false,
            suggestedBy = AuthHelpers.DEFAULT_USER_ID,
            approved = false
        )
        val RESPONSE_FULL_JSON = """
            {
                "id": "$EXISTING_ENTITY_ID",
                "name": "Response Award",
                "description": "The response description",
                "recurring": false,
                "suggestedBy": "${AuthHelpers.DEFAULT_USER_ID}",
                "approved": false
            }
        """.trimIndent()
    }
}
