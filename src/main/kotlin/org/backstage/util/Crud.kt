package org.backstage.util

import javax.ws.rs.core.Response
import kotlin.reflect.KClass

interface SimpleCrudResource<C, U> {
    fun list(): Response

    fun get(id: Long): Response

    fun create(request: C): Response

    fun update(id: Long, request: U): Response

    fun delete(id: Long): Response
}

interface SimpleCrudService<C, U> {
    fun <T : Any> list(responseClass: KClass<T>): List<T>

    fun <T : Any> get(id: Long, responseClass: KClass<T>): T

    fun create(request: C): Long

    fun update(id: Long, request: U)

    fun delete(id: Long)
}
