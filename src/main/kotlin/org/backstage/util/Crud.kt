package org.backstage.util

import java.util.*
import javax.ws.rs.core.Response
import kotlin.reflect.KClass

interface SimpleCrudResource<C, U> {
    fun list(): Response

    fun get(id: UUID): Response

    fun create(request: C): Response

    fun update(id: UUID, request: U): Response

    fun delete(id: UUID): Response
}

interface SimpleCrudService<C, U> {
    fun <T : Any> list(responseClass: KClass<T>): List<T>

    fun <T : Any> get(id: UUID, responseClass: KClass<T>): T

    fun create(request: C): UUID

    fun update(id: UUID, request: U)

    fun delete(id: UUID)
}

enum class CrudOperations { LIST, CREATE, VIEW, UPDATE, DELETE }
