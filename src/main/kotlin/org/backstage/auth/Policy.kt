package org.backstage.auth

import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal
import io.quarkus.security.UnauthorizedException
import io.quarkus.security.identity.SecurityIdentity
import org.backstage.auth.Roles.ROLE_COMMITTEE
import org.backstage.auth.Roles.ROLE_MEMBER
import org.backstage.auth.Roles.ROLE_SUPER_ADMIN

interface HasAuthor {
    val authorId: Any
}

/**
 * Role-Based Access Control (RBAC) does not provide a way to determine whether a given action is authorised if the
 * authorisation logic depends on the entity being accessed. For example, events should only be editable by admins or
 * members with a TEM role (referred to as the "author").
 *
 * This class provides abstraction over the basic handling of authorisation while allowing each domain to dictate what
 * the specific logic for determining access should be, for an entity of type [T]. This also provides some helper
 * methods for common authorisation checks:
 *      - Whether the user is an admin
 *      - Whether the user is a member
 *      - Whether the user is an author
 *
 * This class can be extended and then injected into the service layer.
 *
 * ```
 * @ApplicationScoped
 * class ExamplePolicy(private val identity: SecurityIdentity) : Policy<ExampleEntity>() {
 *      override fun authorise(action: Any, entity: ExampleEntity? = null) = when (action) {
 *          "create" -> allow()
 *          "edit" -> authorise { example -> identity.isAuthor(example) }
 *          else -> deny()
 *      }
 * }
 * ```
 *
 * Alternatively, this can be implemented using a singleton object within the service layer.
 *
 * ```
 * private val policy = object : Policy<ExampleEntity>() {
 *      override fun authorise(action: Any, entity: ExampleEntity? = null) = when (action) {
 *          "create" -> allow()
 *          "edit" -> authorise { example -> identity.isAuthor(example) }
 *          else -> deny()
 *      }
 * }
 * ```
 */
abstract class Policy<T>(private val identity: SecurityIdentity) {
    protected fun allow() = Unit

    protected fun deny() {
        throw UnauthorizedException()
    }

    /**
     * Determine whether to authorise a given [action], which may depend on a given [entity]. This method should throw
     * an [UnauthorizedException] if the action is not allowed, and do nothing if it is. If the authorisation needs to
     * inspect the current user it can use an injected [SecurityIdentity].
     */
    abstract fun authorise(action: Any, entity: T? = null)

    /**
     * Determines whether to authorise a given [action], which may depend on a given [entity], and execute [onAuthorised]
     * if it is. This method should throw an [UnauthorizedException] if the action is not allowed. If the authorisation
     * needs to inspect the current user it can use an injected [SecurityIdentity].
     */
    inline fun <reified U> authoriseAndDo(action: Any, entity: T? = null, onAuthorised: () -> U): U {
        authorise(action, entity)

        return onAuthorised()
    }

    /**
     * Determines whether the current user is authorised based on the result of a given [fn] which takes no arguments.
     * This is useful for determining access for actions with no entity to check, or where the entity does not dictate
     * access permissions.
     */
    protected fun authorise(fn: () -> Boolean) {
        if (!(identity.isAdmin() || fn())) {
            deny()
        }
    }

    /**
     * Determines whether the current user is authorised based on the result of a given [fn], which depends on a given
     * [entity]. A use-case for this is only allowing the original author to modify the [entity]. Access is also denied
     * if no [entity] is provided.
     */
    protected fun authorise(entity: T?, fn: (entity: T) -> Boolean) {
        if (!(identity.isAdmin() || (entity != null && fn(entity)))) {
            deny()
        }
    }

    /**
     * Determines whether the current [SecurityIdentity] is the author of a given [entity], using a [fn] to determine
     * the ID of the author.
     */
    fun SecurityIdentity.isAuthor(entity: T?, fn: (entity: T) -> Any) = when (entity) {
        null -> false
        else -> fn(entity).toString() == this.getUserIdOrNull()
    }

    /**
     * If the given [entity] implements the [HasAuthor] interface this methods offers an alternative way to determine
     * whether the current [SecurityIdentity] is the author, using the [HasAuthor.authorId] property.
     */
    fun SecurityIdentity.isAuthor(entity: HasAuthor?) = when (entity) {
        null -> false
        else -> entity.authorId.toString() == this.getUserIdOrNull()
    }
}

fun SecurityIdentity.isMember() = this.hasRole(ROLE_MEMBER)
fun SecurityIdentity.isAdmin() = this.hasRole(ROLE_COMMITTEE) || this.hasRole(ROLE_SUPER_ADMIN)

fun SecurityIdentity.getUserId(): String = this.getUserIdOrNull()
    ?: throw IllegalStateException("Could not determine ID of user $this")

fun SecurityIdentity.getUserIdOrNull(): String? = when (val principal = this.principal) {
    is OidcJwtCallerPrincipal -> principal.subject
    else -> null
}
