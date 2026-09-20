package userservice.dto;

import userservice.model.User;

import java.time.LocalDateTime;

/**
 * DTO для ответа клиенту. Entity наружу не отдаётся.
 */
public record UserResponseDto(
        Long id,
        String name,
        String email,
        Integer age,
        LocalDateTime createdAt
) {

    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}
