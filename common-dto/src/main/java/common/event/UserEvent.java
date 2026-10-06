package common.event;

public class UserEvent {

    private OperationType operation;
    private String email;

    public UserEvent() {}

    public UserEvent(OperationType operation, String email) {
        this.operation = operation;
        this.email = email;
    }

    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}