package com.github.avereshchagin.ciscolab;

import javax.swing.*;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.Map;

public class ConsolePanel extends JPanel implements KeyListener {

    private final TerminalClient terminalClient;
    private final JTextArea textArea;

    private static class ConsoleCaret extends DefaultCaret {

        private final JTextComponent owner;

        public ConsoleCaret(JTextComponent owner) {
            this.owner = owner;
            owner.putClientProperty("caretAspectRatio", 0.5f);
        }

        @Override
        protected synchronized void damage(Rectangle r) {
            if (r != null) {
                int damageWidth = r.height / 2;
                x = r.x - 4 - damageWidth / 2;
                y = r.y;
                width = 9 + 3 * damageWidth / 2;
                height = r.height;
                repaint();
            }
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform old = g2d.getTransform();
            TextUI mapper = owner.getUI();
            try {
                Rectangle r = mapper.modelToView(owner, owner.getCaretPosition());
                int width = r.height / 2;
                g.setXORMode(Color.BLACK);
                g.translate(width / 2, 0);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            super.paint(g);
            g2d.setTransform(old);
        }
    }

    public ConsolePanel(String hostName, int port, Map<String, String> parameters) {
        super(new BorderLayout());
        textArea = new JTextArea();
        textArea.setLineWrap(false);
        textArea.setEditable(false);

        Font font = new Font("Monospaced", Font.PLAIN, 14);
        textArea.setFont(font);

        ConsoleCaret caret = new ConsoleCaret(textArea);
        textArea.setCaret(caret);
        caret.setVisible(true);
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setFocusable(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        textArea.setFocusTraversalKeysEnabled(false);
        textArea.addKeyListener(this);
        textArea.requestFocus();

        terminalClient = new TerminalClient(textArea, hostName, port, parameters);
        terminalClient.start();
    }

    @Override
    public void keyTyped(KeyEvent event) {
        char ch = event.getKeyChar();
        if (ch != KeyEvent.CHAR_UNDEFINED) {
            if (ch == '\n') {
                ch = '\r';
            }
            terminalClient.sendString(Character.toString(ch));
        }
    }

    private void moveCaretToTheEnd() {
        try {
            textArea.setCaretPosition(textArea.getLineEndOffset(textArea.getLineCount() - 1));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_TAB:
                event.consume();
                terminalClient.sendString("\t");
                break;
            case KeyEvent.VK_UP:
                event.consume();
                terminalClient.sendString("\u001b[A");
                moveCaretToTheEnd();
                break;
            case KeyEvent.VK_DOWN:
                event.consume();
                terminalClient.sendString("\u001b[B");
                moveCaretToTheEnd();
                break;
            case KeyEvent.VK_RIGHT:
                event.consume();
                moveCaretToTheEnd();
                break;
            case KeyEvent.VK_LEFT:
                event.consume();
                moveCaretToTheEnd();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void onDestroy() {
        terminalClient.disconnect();
    }
}
