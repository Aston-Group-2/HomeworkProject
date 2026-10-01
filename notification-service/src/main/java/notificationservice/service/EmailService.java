package notificationservice.service;

import common.event.OperationType;

public interface EmailService {

    void sendNotification(String email, OperationType operation);

    static String buildMessage(OperationType operation) {
        return switch (operation) {
            case CREATE -> "Hello! Your account on site your site has been successfully created.";
            case DELETE -> "Hello! Your account has been deleted.";
        };
    }
}
