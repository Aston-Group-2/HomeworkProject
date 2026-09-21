package userservice.notification.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import userservice.notification.event.UserEvent;
import userservice.notification.services.EmailService;

@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = "user-events",
            groupId = "notification-service"
    )
    public void consume(UserEvent event) {
        emailService.sendUserNotification(event);
    }
}