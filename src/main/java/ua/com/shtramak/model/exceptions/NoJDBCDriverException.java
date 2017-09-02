package ua.com.shtramak.model.exceptions;

public class NoJDBCDriverException extends Exception {
    public NoJDBCDriverException() {
    }

    public NoJDBCDriverException(String message) {
        super(message);
    }
}
