package sirs.remotedocs.domain.exception;

public class RemoteDocsException extends Exception {

    private final ErrorMessage errorMessage;

    public RemoteDocsException(ErrorMessage errorMessage) {
        super(errorMessage.label);
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() { return this.errorMessage; }
}