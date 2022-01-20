package sirs.remotedocs.domain.exception;

public enum ErrorMessage {
    
    INVALID_CREDENTIALS("The username or password are incorrect."),
    INVALID_PASSWORD("The password must have at least 8 characters "),
    INTERNAL_ERROR("There was an error on the server which prevented the operation from being executed."),
    USER_NOT_LOGGED_IN("This user is not logged in."),
    INVALID_SESSION("This session does not contain a valid token/username match."),
    FILE_ALREADY_EXISTS("The file already exists for this user."),
    FILE_DOESNT_EXIST("The file does not exist."),
    UNAUTHORIZED_ACCESS("You do not have enough permissions to access this file."),
    UNAUTHORIZED_WRITE("You do not have enough permissions to edit this file."),
    UNAUTHORIZED_FILENAME_CHANGE("You do not have enough permissions to change this file's name."),
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