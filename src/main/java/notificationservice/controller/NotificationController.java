package notificationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import notificationservice.dto.SendEmailRequest;
import notificationservice.service.EmailService;

/**
 * REST API to send a notification email directly (without Kafka).
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody SendEmailRequest request) {
        emailService.sendNotification(request.getEmail(), request.getOperation());
        return ResponseEntity.ok().build();
    }
}
