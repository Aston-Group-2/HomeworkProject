package gatewayservice;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Сквозные тесты маршрутизации gateway (паттерны API Gateway + Circuit
 * Breaker).
 *
 * <p>
 * Подменяем реальный сервис на MockWebServer и указываем статический URI,
 * чтобы не поднимать Eureka и Config Server.
 *
 * <p>
 * Проверяем две фазы в одном тесте, т.к. после срабатывания circuit breaker
 * (downstream недоступен) маршрут переходит в OPEN и проксирование уже не
 * проверить:
 * сначала успешное проксирование, затем — fallback.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayRoutingTest {

    private static final MockWebServer DOWNSTREAM = new MockWebServer();

    static {
        try {
            DOWNSTREAM.start();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @DynamicPropertySource
    static void gatewayProperties(DynamicPropertyRegistry registry) {
        registry.add("eureka.client.enabled", () -> false);
        registry.add("spring.cloud.config.enabled", () -> false);
        registry.add("spring.cloud.discovery.enabled", () -> false);

        registry.add("spring.cloud.gateway.routes[0].id", () -> "user-service");
        registry.add("spring.cloud.gateway.routes[0].uri", () -> DOWNSTREAM.url("/").toString());
        registry.add("spring.cloud.gateway.routes[0].predicates[0]", () -> "Path=/api/users/**");
        registry.add("spring.cloud.gateway.routes[0].filters[0].name", () -> "CircuitBreaker");
        registry.add("spring.cloud.gateway.routes[0].filters[0].args.name",
                () -> "userServiceCircuitBreaker");
        registry.add("spring.cloud.gateway.routes[0].filters[0].args.fallbackUri",
                () -> "forward:/fallback/user-service");

        // Быстрый переход circuit breaker в открытое состояние для теста
        registry.add("resilience4j.circuitbreaker.instances.userServiceCircuitBreaker.minimum-number-of-calls",
                () -> 1);
        registry.add("resilience4j.circuitbreaker.instances.userServiceCircuitBreaker.sliding-window-size",
                () -> 1);
        registry.add("resilience4j.circuitbreaker.instances.userServiceCircuitBreaker.failure-rate-threshold",
                () -> 50);
    }

    @AfterAll
    static void stopServer() throws IOException {
        DOWNSTREAM.shutdown();
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldProxyThenFallbackWhenDownstreamIsUnavailable() throws IOException, InterruptedException {
        // --- Фаза 1: downstream доступен -> запрос проксируется ---
        DOWNSTREAM.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[{\"id\":1,\"name\":\"Ivan\"}]"));

        webTestClient.get().uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Ivan");

        var recorded = DOWNSTREAM.takeRequest();
        assertThat(recorded.getPath()).isEqualTo("/api/users");

        // --- Фаза 2: гасим downstream -> circuit breaker -> fallback ---
        DOWNSTREAM.shutdown();

        for (int i = 0; i < 3; i++) {
            webTestClient.get().uri("/api/users")
                    .exchange()
                    .expectStatus().isEqualTo(503)
                    .expectBody()
                    .jsonPath("$.message").value(msg -> assertThat((String) msg)
                            .contains("user-service"));
        }
    }
}
