package com.github.avereshchagin.ciscolab;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JFrame {

    public Main() {
        final ConsolePanel consolePanel = new ConsolePanel("91.238.230.93", 8338);

        setSize(600, 400);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                consolePanel.onDestroy();
                System.exit(0);
            }
        });

        setContentPane(consolePanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}
