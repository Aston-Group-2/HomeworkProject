package userservice.notification.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import userservice.notification.event.UserEvent;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendUserNotification(UserEvent event) {

        String subject;
        String text;

        if (event.getOperation() == UserEvent.Operation.CREATED) {
            subject = "Аккаунт успешно создан";
            text = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
        } else {
            subject = "Аккаунт удалён";
            text = "Здравствуйте! Ваш аккаунт был удалён.";
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(event.getEmail());
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}