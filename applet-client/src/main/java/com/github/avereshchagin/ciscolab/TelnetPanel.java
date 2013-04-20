package com.github.avereshchagin.ciscolab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class TelnetPanel extends JPanel implements KeyListener {

    private final TelnetClient telnetClient;

    public TelnetPanel(String hostName, int port) {
        super(new BorderLayout());
        JTextArea textArea = new JTextArea();
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

        telnetClient = new TelnetClient(textArea, hostName, port);
        telnetClient.start();
    }

    @Override
    public void keyTyped(KeyEvent event) {
        char ch = event.getKeyChar();
        if (ch != KeyEvent.CHAR_UNDEFINED) {
            if (ch == '\n') {
                ch = '\r';
            }
            telnetClient.sendString(Character.toString(ch));
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void onAppletDestroy() {
        telnetClient.disconnect();
    }
}
