package ua.com.shtramak.controller.command;

public interface Command {
    boolean isDetected(String command);

    void execute(String command);
}
