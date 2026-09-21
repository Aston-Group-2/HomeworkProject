package userservice.notification;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.icegreen.greenmail.junit5.GreenMailExtension;

import userservice.notification.event.UserEvent;

import static com.icegreen.greenmail.util.ServerSetupTest.SMTP;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = "user-events"
)
class NotificationIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail =
            new GreenMailExtension(SMTP);

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        registry.add(
                "spring.kafka.bootstrap-servers",
                () -> System.getProperty(
                        "spring.embedded.kafka.brokers"
                )
        );

        registry.add(
                "spring.mail.host",
                () -> "localhost"
        );

        registry.add(
                "spring.mail.port",
                () -> greenMail.getSmtp().getPort()
        );

        registry.add(
                "spring.mail.username",
                () -> "test@example.com"
        );

        registry.add(
                "spring.mail.password",
                () -> "test"
        );
    }

    @Test
    void shouldSendCreatedEmailThroughKafka() throws Exception {

        String email = "created@example.com";
        UserEvent event = new UserEvent(UserEvent.Operation.CREATED, email);
        kafkaTemplate.send("user-events", email, event);
        greenMail.waitForIncomingEmail(5000, 1);
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(1);
        Message message = messages[0];
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(email);
        assertThat(message.getSubject()).isEqualTo("Аккаунт успешно создан");
        assertThat(message.getContent().toString()).contains("Ваш аккаунт на сайте ваш сайт был успешно создан");
    }

    @Test
    void shouldSendDeletedEmailThroughKafka() throws Exception {

        String email = "deleted@example.com";
        UserEvent event = new UserEvent(UserEvent.Operation.DELETED, email);
        kafkaTemplate.send("user-events", email, event);
        greenMail.waitForIncomingEmail(5000, 1);
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(1);
        Message message = messages[0];
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(email);
        assertThat(message.getSubject()).isEqualTo("Аккаунт удалён");
        assertThat(message.getContent().toString()).contains("Ваш аккаунт был удалён");
    }
}