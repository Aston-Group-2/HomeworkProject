package notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import notificationservice.dto.SendEmailRequest;
import notificationservice.service.EmailService;

/**
 * REST API для отправки уведомлений.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "API для отправки уведомлений")
public class NotificationController {

    private final EmailService emailService;

    @Operation(summary = "Отправить email уведомление")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Уведомление отправлено"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody SendEmailRequest request) {
        emailService.sendNotification(request.getEmail(), request.getOperation());
        return ResponseEntity.ok().build();
    }
}
