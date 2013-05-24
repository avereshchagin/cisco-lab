package com.github.avereshchagin.ciscolab;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class Main extends JFrame {
    public Main(String host, int port, Map<String, String> parameters) {
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
                boolean validArguments = false;
                String host = "";
                int port = 0;
                Map<String, String> parameters = new HashMap<String, String>();
                if (args.length >= 4) {
                    validArguments = true;
                    host = args[0].trim();
                    try {
                        port = Integer.parseInt(args[1].trim());
                    } catch (NumberFormatException e) {
                        validArguments = false;
                    }
                    parameters.put("accessToken", args[2].trim());
                    String deviceId = args[3].trim();
                    if (deviceId.matches("\\d+")) {
                        parameters.put("deviceId", String.valueOf(deviceId));
                    } else {
                        validArguments = false;
                    }
                }
                if (validArguments) {
                    new Main(host, port, parameters).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Invalid command line arguments.", "Error starting application",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
