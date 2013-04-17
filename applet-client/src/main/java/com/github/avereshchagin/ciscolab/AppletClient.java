package com.github.avereshchagin.ciscolab;

import java.applet.Applet;
import java.awt.*;

public class AppletClient extends Applet {

    @Override
    public void init() {

    }

    @Override
    public void paint(Graphics g) {
        g.drawString("It works", 50, 50);
    }
}
