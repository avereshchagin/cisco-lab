package com.github.avereshchagin.ciscolab;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class AppletClient extends JApplet {

    private TelnetPanel telnetPanel;

    @Override
    public void init() {
        String hostName = getParameter("hostname");
        System.out.println(hostName);

        try {
            telnetPanel = new TelnetPanel();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    setContentPane(telnetPanel);
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
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        telnetPanel.onAppletDestroy();
                    }
                });
            } catch (InterruptedException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
