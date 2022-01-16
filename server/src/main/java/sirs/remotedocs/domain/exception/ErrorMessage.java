package sirs.remotedocs.domain.exception;

public enum ErrorMessage {
    
    INVALID_CREDENTIALS("The username or password are incorrect"),
    USER_ALREADY_EXISTS("This username already exists.");
    
    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}