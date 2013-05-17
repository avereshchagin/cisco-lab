package com.github.avereshchagin.ciscolab;

import com.github.avereshchagin.ciscolab.proxy.Server;
import com.github.avereshchagin.ciscolab.rest.Rack;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException {
        new Rack().start();
        new Server().loop();
    }
}
