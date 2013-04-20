package com.github.avereshchagin.ciscolab;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class TelnetClient extends Thread {

    private static final int BUFFER_SIZE = 10240;

    private final JTextArea textArea;
    private final String hostName;
    private final int port;

    private Socket socket;
    private InputStream remoteIn;
    private OutputStream remoteOut;

    private AtomicBoolean active = new AtomicBoolean(false);

    public TelnetClient(JTextArea textArea, String hostName, int port) {
        this.textArea = textArea;
        this.hostName = hostName;
        this.port = port;
    }

    @Override
    public void run() {
        appendText("Connecting to " + hostName + ":" + port + "...\n");

        try {
            socket = new Socket(hostName, port);
            remoteIn = socket.getInputStream();
            remoteOut = socket.getOutputStream();
        } catch (IOException e) {
            appendText("An error occurred while connecting.\n");
            return;
        }

        appendText("Connected.\n");
        active.compareAndSet(false, true);

        byte[] data = new byte[BUFFER_SIZE];
        while (active.get()) {
            int len;
            try {
                len = remoteIn.read(data, 0, BUFFER_SIZE);
            } catch (IOException e) {
                active.compareAndSet(true, false);
                break;
            }
            if (len == -1) {
                active.compareAndSet(true, false);
                break;
            }
            if (len > 0) {
                try {
                    appendText(new String(data, 0, len, "ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        appendText("\nConnection has been closed.\n");
        try {
            socket.close();
        } catch (IOException ignored) {
        }

    }

    private void appendText(final String text) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                // Must be executed in UI thread
                @Override
                public void run() {
                    String appendingText;
                    if (text.startsWith("\b")) {
                        int position = textArea.getCaretPosition();
                        textArea.setCaretPosition(position - 1);
                        textArea.replaceRange("", position - 1, position);
                        appendingText = text.substring(1);
                    } else {
                        appendingText = text;
                    }
                    // Deleting 'bell' symbols
                    appendingText = appendingText.replaceAll("\u0007", "");
                    // Deleting 'backspace' pairs
                    appendingText = appendingText.replaceAll(".\b", "");
                    textArea.append(appendingText);
                }
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void sendString(String data) {
        if (active.get()) {
            try {
                remoteOut.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        active.compareAndSet(true, false);
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
}
