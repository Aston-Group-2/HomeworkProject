package userservice.kafka;

import common.event.OperationType;
import common.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public void send(String email, OperationType operation) {
        UserEvent event = new UserEvent(operation, email);
        kafkaTemplate.send(topic, email, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send event {} to Kafka", event, ex);
                    } else {
                        log.info("Event sent to Kafka: {}", event);
                    }
                });
    }
}
