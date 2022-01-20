package sirs.remotedocs.domain.exception;

public enum ErrorMessage {
    
    INVALID_CREDENTIALS("The username or password are incorrect."),
    INVALID_PASSWORD("The password must have at least 8 characters "),
    INTERNAL_ERROR("There was an error on the server which prevented the operation from being executed."),
    USER_ALREADY_EXISTS("The chosen username is not valid, please try another username");
    
    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}