package com.reproducer.propagationWebClient
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.session.WebSessionIdResolver

/**
 * Session ID resolver that never retrieves nor updates session ID in server web exchange.
 * It can be used to discard SESSION cookie and enforce stateless behavior in API calls.
 */
class NoOpWebSessionIdResolver : WebSessionIdResolver {

    override fun resolveSessionIds(exchange: ServerWebExchange) = emptyList<String>()

    override fun setSessionId(exchange: ServerWebExchange, sessionId: String) = Unit

    override fun expireSession(exchange: ServerWebExchange) = Unit
}
