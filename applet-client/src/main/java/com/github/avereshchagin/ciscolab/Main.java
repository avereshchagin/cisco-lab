package com.github.avereshchagin.ciscolab;

import com.google.gson.Gson;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JFrame {

    public Main(String host, int port, ConnectionParameters parameters) {
        final ConsolePanel consolePanel = new ConsolePanel(host, port, parameters);

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

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                boolean validParameters = false;
                String host = "";
                int port = 0;
                String accessToken = "";
                int deviceId = 0;
                if (args.length >= 4) {
                    validParameters = true;
                    host = args[0];
                    try {
                        port = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        validParameters = false;
                    }
                    accessToken = args[2];
                    try {
                        deviceId = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        validParameters = false;
                    }
                }
                if (validParameters) {
                    new Main(host, port, new ConnectionParameters(accessToken, deviceId)).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Invalid command line parameters.", "Error starting application",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
