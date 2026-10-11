package notificationservice.service;

import common.event.OperationType;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Value("${app.mail.from}")
    private String from;

    @Override
    public void sendNotification(String email, OperationType operation) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("emailService");

        try {
            circuitBreaker.executeRunnable(() -> sendEmail(email, operation));
        } catch (Exception e) {
            log.warn("Circuit Breaker is OPEN or error occurred for email: {}. Fallback triggered.", email);
        }
    }

    private void sendEmail(String email, OperationType operation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Account notification");
        message.setText(EmailService.buildMessage(operation));

        mailSender.send(message);
        log.info("Notification email sent to {} for operation {}", email, operation);
    }
}