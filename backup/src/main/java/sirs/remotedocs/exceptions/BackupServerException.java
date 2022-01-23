package sirs.remotedocs.exceptions;

public class BackupServerException extends Exception {

    private String errorMessage;

    public BackupServerException(String errorMessage) {
       this.errorMessage = errorMessage;
    }

    public String getMessage() { return this.errorMessage; }
}