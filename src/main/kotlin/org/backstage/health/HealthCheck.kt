package org.backstage.health

import org.eclipse.microprofile.health.HealthCheck
import org.eclipse.microprofile.health.HealthCheckResponse
import org.eclipse.microprofile.health.Liveness
import javax.enterprise.context.ApplicationScoped

@Liveness
@ApplicationScoped
class SimpleHealthCheck : HealthCheck {
    override fun call(): HealthCheckResponse = HealthCheckResponse.up(APP_NAME)

    companion object {
        private const val APP_NAME = "APP"
    }
}
