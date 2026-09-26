package notificationservice;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import common.event.OperationType;
import common.event.UserEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.kafka.test.context.EmbeddedKafka;

import jakarta.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "user-events")
@TestPropertySource(properties = {
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.consumer.auto-offset-reset=earliest"
})
class NotificationIntegrationTest {

        @RegisterExtension
        static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
                        .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "pass"))
                        .withPerMethodLifecycle(false);

        @DynamicPropertySource
        static void mailProperties(DynamicPropertyRegistry registry) {
                registry.add("spring.mail.host", () -> "localhost");
                registry.add("spring.mail.port", () -> ServerSetupTest.SMTP.getPort());
                registry.add("spring.mail.username", () -> "user");
                registry.add("spring.mail.password", () -> "pass");
                registry.add("spring.mail.properties.mail.smtp.auth", () -> true);
        }

        @Autowired
        private KafkaTemplate<String, UserEvent> kafkaTemplate;

        @Test
        void whenCreateEventSent_thenEmailSentWithCreateMessage() throws Exception {
                String recipient = "create-user@test.com";
                kafkaTemplate.send("user-events", recipient,
                                new UserEvent(OperationType.CREATE, recipient)).get();

                await().atMost(Duration.ofSeconds(10))
                                .untilAsserted(() -> {
                                        MimeMessage message = findMessageFor(recipient);
                                        assertThat(message.getContent().toString())
                                                        .isEqualTo("Hello! Your account on site your site has been successfully created.");
                                });
        }

        @Test
        void whenDeleteEventSent_thenEmailSentWithDeleteMessage() throws Exception {
                String recipient = "delete-user@test.com";
                kafkaTemplate.send("user-events", recipient,
                                new UserEvent(OperationType.DELETE, recipient)).get();

                await().atMost(Duration.ofSeconds(10))
                                .untilAsserted(() -> {
                                        MimeMessage message = findMessageFor(recipient);
                                        assertThat(message.getContent().toString())
                                                        .isEqualTo("Hello! Your account has been deleted.");
                                });
        }

        /**
         * Finds the single message delivered to the given recipient.
         * Uses a per-test recipient address so messages from other tests do not
         * interfere.
         */
        private MimeMessage findMessageFor(String recipient) {
                MimeMessage[] messages = greenMail.getReceivedMessages();
                MimeMessage found = null;
                for (MimeMessage message : messages) {
                        try {
                                if (message.getAllRecipients().length > 0
                                                && recipient.equals(message.getAllRecipients()[0].toString())) {
                                        found = message;
                                }
                        } catch (Exception ignored) {
                                // ignore malformed messages
                        }
                }
                assertThat(found).as("message for %s", recipient).isNotNull();
                return found;
        }
}
