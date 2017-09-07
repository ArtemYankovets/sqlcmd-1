package ua.com.shtramak.sqlcmd.integration;

import java.io.IOException;
import java.io.InputStream;

class ConsoleInputStream extends InputStream {
    private String commands;
    private boolean firstRead = true;
    private byte[] bytes;
    private boolean exit;

    @Override
    public int read() throws IOException {
        if (commands.length() == 0)
            return -1;

        if (firstRead) {
            bytes = commands.getBytes("UTF-8");
            firstRead = false;
        }

        if (exit){
            exit=false;
            return -1;
        }

        byte currByte = getByte(bytes);
        if (currByte == '\n') {
            exit = true;
        }
        return currByte;
    }

    private byte getByte(byte[] bytes) {
        if (bytes.length == 0) return -1;

        byte currByte = bytes[0];

        byte[] tmp = new byte[bytes.length - 1];
        System.arraycopy(bytes, 1, tmp, 0, bytes.length - 1);
        this.bytes = tmp;

        return currByte;
    }

    public void addCommand(String command) {
        if (commands == null)
            commands = command + "\n";
        else
            commands += command + "\n";
    }
}
