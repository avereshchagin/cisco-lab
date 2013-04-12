package com.github.avereshchagin.ciscolab;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.loop();
    }
}
