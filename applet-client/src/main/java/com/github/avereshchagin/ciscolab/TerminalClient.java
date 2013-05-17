package com.github.avereshchagin.ciscolab;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class TerminalClient extends Thread {

    private static final int BUFFER_SIZE = 10240;

    private final JTextArea textArea;
    private final String hostName;
    private final int port;

    private Socket socket;
    private InputStream remoteIn;
    private OutputStream remoteOut;

    private AtomicBoolean active = new AtomicBoolean(false);

    public TerminalClient(JTextArea textArea, String hostName, int port) {
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

    private static Pair<String, Integer> preprocessText(String source) {
        int shiftLeft = 0;
        int pos = 0;
        char[] result = new char[source.length()];
        for (int i = 0; i < source.length(); i++) {
            char ch = source.charAt(i);
            if (ch == '\u0007') {
                Toolkit.getDefaultToolkit().beep();
                continue;
            }
            if (ch == '\b') {
                if (pos == 0) {
                    shiftLeft++;
                } else {
                    pos--;
                }
                continue;
            }
            result[pos] = ch;
            pos++;
        }
        return new Pair<String, Integer>(String.copyValueOf(result, 0, pos), shiftLeft);
    }

    private void appendText(final String text) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                // Must be executed in UI thread
                @Override
                public void run() {
                    Pair<String, Integer> p = preprocessText(text);
                    String appendingText = p.getFirst();
                    int shiftLeft = p.getSecond();
                    if (shiftLeft > 0) {
                        try {
                            textArea.setCaretPosition(textArea.getLineEndOffset(textArea.getLineCount() - 1));
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                        int position = textArea.getCaretPosition();
                        textArea.setCaretPosition(position - shiftLeft);
                        textArea.replaceRange("", position - shiftLeft, position);
                    }
                    textArea.append(appendingText);
                    try {
                        textArea.setCaretPosition(textArea.getLineEndOffset(textArea.getLineCount() - 1));
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
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