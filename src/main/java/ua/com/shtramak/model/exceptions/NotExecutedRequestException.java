package ua.com.shtramak.model.exceptions;

import java.sql.SQLException;

public class NotExecutedRequestException extends Throwable {
    public NotExecutedRequestException() {
    }

    public NotExecutedRequestException(String message) {
        super(message);
    }
}
