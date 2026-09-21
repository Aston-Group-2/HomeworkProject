package userservice.notification.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import userservice.notification.dto.NotificationRequest;
import userservice.notification.event.UserEvent;
import userservice.notification.services.EmailService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(
            @Valid @RequestBody NotificationRequest request) {

        UserEvent event = new UserEvent(
                request.operation(),
                request.email()
        );

        emailService.sendUserNotification(event);

        return ResponseEntity.ok().build();
    }
}