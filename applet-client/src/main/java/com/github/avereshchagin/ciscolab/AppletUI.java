package com.github.avereshchagin.ciscolab;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class AppletUI extends JApplet {

    private ConsolePanel consolePanel;

    @Override
    public void init() {
        String hostName = getParameter("hostname");
        int port;
        try {
            port = Integer.parseInt(getParameter("port"));
        } catch (NumberFormatException e) {
            port = 0;
        }
        String accessToken = getParameter("accessToken");
        int deviceId;
        try {
            deviceId = Integer.parseInt(getParameter("deviceId"));
        } catch (NumberFormatException e) {
            deviceId = 0;
        }

        final Container container;
        if (hostName != null && port > 0) {
            consolePanel = new ConsolePanel(hostName, port, new ConnectionParameters(accessToken, deviceId));
            container = consolePanel;
        } else {
            container = new JLabel("<html><span color=\"#ff0000\">Error: Invalid applet parameters</span>");
        }

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    setContentPane(container);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (consolePanel != null) {
            consolePanel.onDestroy();
        }
    }
}
