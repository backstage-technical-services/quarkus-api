package org.backstage

import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import io.quarkus.runtime.configuration.ProfileManager
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

@ApplicationScoped
class Lifecycle {
    private val logger = Logger.getLogger(this::class.java.canonicalName)

    fun onStart(@Observes event: StartupEvent) {
        logger.info("Started application with profile: ${ProfileManager.getActiveProfile()}")
    }

    fun onStop(@Observes event: ShutdownEvent) {
        logger.info("Stopping application cleanly ...")
    }
}