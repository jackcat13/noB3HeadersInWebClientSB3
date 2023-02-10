package com.reproducer.propagationWebClient

import io.micrometer.context.ContextSnapshot
import io.micrometer.tracing.TraceContext
import io.micrometer.tracing.Tracer
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(LOWEST_PRECEDENCE)
class TracingWebFilter(private val tracer: Tracer) : WebFilter {

    companion object{
        val TRACING_KEY = TraceContext::class.java
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return Mono.deferContextual {
            ContextSnapshot.captureAll(it).setThreadLocals()
            chain.filter(exchange).contextWrite {
                tracer.currentTraceContext().context()?.let { traceContext ->
                    it.put(TRACING_KEY, traceContext)
                }
            }
        }
    }
}
