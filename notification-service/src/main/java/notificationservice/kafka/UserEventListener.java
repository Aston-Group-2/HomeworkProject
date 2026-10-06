package notificationservice.kafka;

import common.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import notificationservice.service.EmailService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final EmailService emailService;

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onUserEvent(UserEvent event) {
        log.info("Received user event: {}", event);
        emailService.sendNotification(event.getEmail(), event.getOperation());
    }
}
