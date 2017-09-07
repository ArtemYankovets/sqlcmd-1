package ua.com.shtramak.sqlcmd.view;

public interface View {
    void writeln(String message);

    void write(String message);

    String read();

}