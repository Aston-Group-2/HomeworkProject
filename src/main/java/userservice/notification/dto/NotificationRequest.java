package userservice.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import userservice.notification.event.UserEvent;

public record NotificationRequest(@NotBlank @Email String email, @NotNull UserEvent.Operation operation) {
}