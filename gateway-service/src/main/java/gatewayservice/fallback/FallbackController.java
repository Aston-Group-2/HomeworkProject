package gatewayservice.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallback() {
        return Mono.just(buildResponse("user-service"));
    }

    @PostMapping("/user-service")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallbackPost() {
        return Mono.just(buildResponse("user-service"));
    }

    @GetMapping("/notification-service")
    public Mono<ResponseEntity<Map<String, Object>>> notificationServiceFallback() {
        return Mono.just(buildResponse("notification-service"));
    }

    @PostMapping("/notification-service")
    public Mono<ResponseEntity<Map<String, Object>>> notificationServiceFallbackPost() {
        return Mono.just(buildResponse("notification-service"));
    }

    private ResponseEntity<Map<String, Object>> buildResponse(String service) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "message", "Service '" + service + "' is temporarily unavailable. Please try again later.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}
