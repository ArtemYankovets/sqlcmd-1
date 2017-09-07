package ua.com.shtramak.sqlcmd.model.exceptions;

public class UnsuccessfulConnectionException extends Exception {
    public UnsuccessfulConnectionException() {
    }

    public UnsuccessfulConnectionException(String message) {
        super(message);
    }
}
