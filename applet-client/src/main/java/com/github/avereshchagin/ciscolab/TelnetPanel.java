package com.github.avereshchagin.ciscolab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class TelnetPanel extends JPanel implements KeyListener {

    private final TelnetClient telnetClient;

    public TelnetPanel(String hostName, int port) {
        super(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(false);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setFocusable(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        textArea.setFocusTraversalKeysEnabled(false);
        textArea.addKeyListener(this);
        textArea.requestFocus();

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
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_TAB:
                event.consume();
                telnetClient.sendString("\t");
                break;
            case KeyEvent.VK_UP:
                event.consume();
                telnetClient.sendString("\u001b[A");
                break;
            case KeyEvent.VK_DOWN:
                event.consume();
                telnetClient.sendString("\u001b[B");
                break;
            case KeyEvent.VK_RIGHT:
                event.consume();
                break;
            case KeyEvent.VK_LEFT:
                event.consume();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void onAppletDestroy() {
        telnetClient.disconnect();
    }
}
