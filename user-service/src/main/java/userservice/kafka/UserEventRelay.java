package userservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import userservice.event.UserChangedEvent;

@Component
@RequiredArgsConstructor
public class UserEventRelay {

    private final UserEventProducer userEventProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserChanged(UserChangedEvent event) {
        userEventProducer.send(event.email(), event.operation());
    }
}
