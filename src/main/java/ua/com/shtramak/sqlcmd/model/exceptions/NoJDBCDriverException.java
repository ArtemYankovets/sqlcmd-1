package ua.com.shtramak.sqlcmd.model.exceptions;

public class NoJDBCDriverException extends Exception {
    public NoJDBCDriverException() {
    }

    public NoJDBCDriverException(String message) {
        super(message);
    }
}
