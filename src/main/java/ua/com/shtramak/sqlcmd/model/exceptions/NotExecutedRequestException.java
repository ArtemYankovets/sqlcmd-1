package ua.com.shtramak.sqlcmd.model.exceptions;

public class NotExecutedRequestException extends Throwable {
    public NotExecutedRequestException() {
    }

    public NotExecutedRequestException(String message) {
        super(message);
    }
}
