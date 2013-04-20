package com.github.avereshchagin.ciscolab;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class AppletClient extends JApplet {

    private TelnetPanel telnetPanel;

    @Override
    public void init() {
        String hostName = getParameter("hostname");
        int port;
        try {
            port = Integer.parseInt(getParameter("port"));
        } catch (NumberFormatException e) {
            port = 0;
        }

        final Container container;
        if (hostName != null && port > 0) {
            telnetPanel = new TelnetPanel(hostName, port);
            container = telnetPanel;
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
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (telnetPanel != null) {
            telnetPanel.onAppletDestroy();
        }
    }
}
