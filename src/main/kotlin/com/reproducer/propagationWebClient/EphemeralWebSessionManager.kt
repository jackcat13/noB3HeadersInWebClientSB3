package com.reproducer.propagationWebClient

import org.springframework.web.server.session.DefaultWebSessionManager
import org.springframework.web.server.session.WebSessionManager

/**
 * Session manager that expires sessions based on [SecurityWebSessionProperties.expiration] and removes when
 * [SecurityWebSessionProperties.maxSession] threshold is exceeded
 *
 * @see DefaultWebSessionManager
 * @param properties web session properties
 */
class EphemeralWebSessionManager(properties: SecurityWebSessionProperties) : WebSessionManager by defaultWebSessionManager(properties) {

    companion object {

        private fun defaultWebSessionManager(properties: SecurityWebSessionProperties): WebSessionManager {
            return DefaultWebSessionManager().apply {
                sessionStore = EphemeralWebSessionStore(properties.expiration, properties.maxSession)
                sessionIdResolver = NoOpWebSessionIdResolver()
            }
        }
    }
}
