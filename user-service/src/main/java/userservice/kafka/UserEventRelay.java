package userservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import userservice.event.UserChangedEvent;

/**
 * Пересылает события пользователя в Kafka только после успешного коммита транзакции.
 * При откате события отбрасываются, поэтому письмо не уйдёт о несуществующем пользователе.
 */
@Component
@RequiredArgsConstructor
public class UserEventRelay {

    private final UserEventProducer userEventProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserChanged(UserChangedEvent event) {
        userEventProducer.send(event.email(), event.operation());
    }
}
