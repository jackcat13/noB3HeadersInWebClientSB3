package com.reproducer.propagationWebClient

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.reproducer.propagationWebClient.RemoteJwtDecoderHttpTest.Companion.defaultWebSessionManager
import com.reproducer.propagationWebClient.TestWebApplication.User
import com.reproducer.propagationWebClient.Token.loadResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody


@AutoConfigureWebTestClient
abstract class RemoteJwtDecoderHttpTest(@Autowired protected var webTestClient: WebTestClient) {

    private val wireMockServer = WireMockServer(wireMockConfig().port(9080))

    companion object{
        internal val defaultWebSessionManager = EphemeralWebSessionManager(SecurityWebSessionProperties())
    }

    @BeforeEach
    fun beforeEach() {
        webTestClient = webTestClient.withWebSession()
        wireMockServer.start()
    }

    @AfterEach
    fun afterEach() {
        wireMockServer.stop()
    }

}

internal fun WebTestClient.withWebSession(): WebTestClient {
    return mutateWith { _, webHttpHandlerBuilder, _ -> webHttpHandlerBuilder?.sessionManager(defaultWebSessionManager) }
}


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
internal class TracingRemoteTest(@Autowired webTestClient: WebTestClient) : RemoteJwtDecoderHttpTest(webTestClient) {

    companion object{
        internal val TOKEN = loadResource("valid_token.txt")
    }

    @Test
    fun `should propagate tracing headers when fetching remote jwks`() {
        webTestClient.get()
            .uri("users/all")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $TOKEN")
            .header("X-B3-TraceId", "463ac35c9f6413ad48485a3953bb6124")
            .header("X-B3-SpanId", "a2fb4a1d1a96d312")
            .header("X-B3-ParentSpanId", "0020000000000001")
            .header("X-B3-Flags", "1")
            .exchange()
            .expectStatus().isOk
            .expectBody<User>()
            .returnResult().responseBody!!.apply { assertThat(username).isEqualTo("all") }
    }
}

object Token {

    fun loadResource(path: String) = object {}.javaClass.classLoader.getResource(path)!!.readText().trim()
}