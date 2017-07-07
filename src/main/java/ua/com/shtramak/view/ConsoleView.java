package ua.com.shtramak.view;

import java.util.Scanner;

public class ConsoleView implements View {
    @Override
    public void writeln(String message) {
        System.out.println(message);
    }

    @Override
    public void write(String message) {
        System.out.print(message);
    }

    @Override
    public String read() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
