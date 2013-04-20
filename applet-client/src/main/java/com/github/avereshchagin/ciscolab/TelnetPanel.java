package com.github.avereshchagin.ciscolab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

public class TelnetPanel extends JPanel implements KeyListener {

    private static final int BUFFER_SIZE = 10240;

    private final JTextArea textArea = new JTextArea();

    private Socket socket;
    private final InputStream remoteIn;
    private final OutputStream remoteOut;

    private static String processBackspaces(String source) {
        // Deleting 'bell' symbols
        source = source.replaceAll("\u0007", "");
        // Deleting 'backspace' pairs
        return source.replaceAll(".\b", "");
    }

    public TelnetPanel() throws IOException {
        super(new BorderLayout());
        textArea.setFocusable(false);
        textArea.setLineWrap(false);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setFocusable(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        addKeyListener(this);
        setFocusable(true);
        requestFocus();

        socket = new Socket("91.238.230.93", 8338);
        remoteIn = socket.getInputStream();
        remoteOut = socket.getOutputStream();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final byte[] data = new byte[BUFFER_SIZE];
                try {
                    while (true) {
                        int len = -1;
                        if (socket != null) {
                            len = remoteIn.read(data, 0, BUFFER_SIZE);
                        }
                        if (len == -1) {
                            appendText("\nConnection has been closed by the remote side.\n");
                            socket.close();
                            socket = null;
                            break;
                        } else {
                            try {
                                appendText(new String(data, 0, len, "ISO-8859-1"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void appendText(final String text) {
        // Must be executed in UI thread
        try {
            EventQueue.invokeAndWait(new Runnable() {
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

    @Override
    public void keyTyped(KeyEvent event) {
        char ch = event.getKeyChar();
        if (ch != KeyEvent.CHAR_UNDEFINED) {
            if (ch == '\n') {
                ch = '\r';
            }
            try {
                if (socket != null) {
                    remoteOut.write(Character.toString(ch).getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void onAppletDestroy() {
        System.out.println("onAppletDestroy");
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }
}
