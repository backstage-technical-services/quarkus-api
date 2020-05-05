package org.backstage.elections.position

import org.valiktor.functions.isNotNull
import org.valiktor.validate

data class CreateElectionPosition(
    val name: String
) {
    init {
        validate(this) {
            validate(CreateElectionPosition::name)
                .isNotNull()
        }
    }
}

data class UpdateElectionPosition(
    val name: String
) {
    init {
        validate(this) {
            validate(UpdateElectionPosition::name)
                .isNotNull()
        }
    }
}
