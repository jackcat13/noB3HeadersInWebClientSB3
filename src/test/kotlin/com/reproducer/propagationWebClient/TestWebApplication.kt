package com.reproducer.propagationWebClient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

private const val BASE_URI = "http://localhost:9080/.well-known"

/**
 * Starts a web application for testing purpose
 */
@SpringBootApplication(scanBasePackages = ["com.reproducer.propagationWebClient"], proxyBeanMethods = false)
class TestWebApplication() {

    @Configuration(proxyBeanMethods = false)
    class ExtendedSecurityConfiguration {

        companion object {

            const val ENABLE_CUSTOMER_MAPPER = "enableCustomMapperTest"
        }
    }

    @RestController
    @RequestMapping("/users")
    class UserResource(private val webClientBuilder: WebClient.Builder) {

        val webClient = webClientBuilder.baseUrl("$BASE_URI/jwks-tracing").build()

        @GetMapping("/all")
        fun all(): Mono<User> {
            return webClient.get().retrieve().bodyToMono<Any>().map {
                User("all")
            }
        }
    }

    data class User(val username: String)

}
