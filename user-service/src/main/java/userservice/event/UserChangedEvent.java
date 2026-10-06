package userservice.event;

import common.event.OperationType;

/**
 * Внутреннее событие приложения: пользователь создан или удалён.
 * Публикуется внутри транзакции, но обрабатывается только после её успешного коммита.
 */
public record UserChangedEvent(String email, OperationType operation) {
}
