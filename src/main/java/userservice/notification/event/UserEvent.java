package userservice.notification.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {

    public enum Operation {
        CREATED,
        DELETED
    }

    private Operation operation;
    private String email;
}