package gatewayservice.fallback;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(FallbackController.class)
class FallbackControllerTest {

        @Autowired
        private WebTestClient webTestClient;

        @Test
        void userServiceFallback_ShouldReturn503WithMessage() {
                webTestClient.get().uri("/fallback/user-service")
                                .exchange()
                                .expectStatus().isEqualTo(503)
                                .expectBody()
                                .jsonPath("$.status").isEqualTo(503)
                                .jsonPath("$.message").isEqualTo(
                                                "Service 'user-service' is temporarily unavailable. Please try again later.")
                                .jsonPath("$.timestamp").exists();
        }

        @Test
        void userServiceFallbackPost_ShouldReturn503() {
                webTestClient.post().uri("/fallback/user-service")
                                .exchange()
                                .expectStatus().isEqualTo(503)
                                .expectBody()
                                .jsonPath("$.message").isEqualTo(
                                                "Service 'user-service' is temporarily unavailable. Please try again later.");
        }

        @Test
        void notificationServiceFallback_ShouldReturn503WithMessage() {
                webTestClient.get().uri("/fallback/notification-service")
                                .exchange()
                                .expectStatus().isEqualTo(503)
                                .expectBody()
                                .jsonPath("$.status").isEqualTo(503)
                                .jsonPath("$.message").isEqualTo(
                                                "Service 'notification-service' is temporarily unavailable. Please try again later.");
        }

        @Test
        void notificationServiceFallbackPost_ShouldReturn503() {
                webTestClient.post().uri("/fallback/notification-service")
                                .exchange()
                                .expectStatus().isEqualTo(503)
                                .expectBody()
                                .jsonPath("$.message").isEqualTo(
                                                "Service 'notification-service' is temporarily unavailable. Please try again later.");
        }
}
