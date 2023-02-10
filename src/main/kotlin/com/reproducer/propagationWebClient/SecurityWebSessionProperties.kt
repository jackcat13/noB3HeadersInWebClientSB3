package com.reproducer.propagationWebClient

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration
import java.time.Duration.ZERO

@ConfigurationProperties("security.web-session")
data class SecurityWebSessionProperties(
        /**
         * The duration the web session is held before expiration.
         * If [expiration] is set to [ZERO], session is immediately expired and SESSION cookie is never sent back to the caller.
         */
        val expiration: Duration = Duration.ofSeconds(5),
        /**
         * The maximum concurrent active session that can be stored.
         * Once the limit is reached, any attempt to store an additional session will result in an IllegalStateException.
         */
        val maxSession: Int = 100_000
) {

    companion object {

        const val WEB_SESSION_MANAGER_ENABLED = "security.web-session.sbcp-manager"
    }
}
