package com.reproducer.propagationWebClient

import org.springframework.web.server.WebSession
import org.springframework.web.server.session.InMemoryWebSessionStore
import org.springframework.web.server.session.WebSessionStore
import reactor.core.publisher.Mono
import java.time.Duration

/**
 * Map based [WebSessionStore] that stores session for a short amount of time.
 * When is authenticated through security filters, it's session is stored for [MAX_TIMEOUT_DURATION].
 *
 * @see InMemoryWebSessionStore
 */
class EphemeralWebSessionStore(private val expiration: Duration, maxSession: Int) : InMemoryWebSessionStore() {

    init {
        maxSessions = maxSession
    }

    override fun createWebSession(): Mono<WebSession> = super.createWebSession().map { it.apply { maxIdleTime = expiration } }
}
