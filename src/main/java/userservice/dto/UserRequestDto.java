package userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * DTO для создания/обновления пользователя.
 * Принимается контроллером вместо entity.
 */
public record UserRequestDto(

                @NotBlank(message = "Имя не может быть пустым") String name,

                @NotBlank(message = "Email не может быть пустым") @Email(message = "Email должен быть корректным адресом") String email,

                @Positive(message = "Возраст должен быть положительным") int age) {
}
