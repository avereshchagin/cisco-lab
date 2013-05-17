package com.github.avereshchagin.ciscolab.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final int SERVER_PORT = 8338;
    private static final int NUMBER_OF_THREADS = 12;

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    public Server() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        LOGGER.info("Server has been created");
    }

    public void loop() {
        LOGGER.info("Server has been started");
        try {
            while (true) {
                pool.execute(new ConnectionHandler(serverSocket.accept()));
            }
        } catch (IOException e) {
            pool.shutdown();
            LOGGER.log(Level.SEVERE, "loop", e);
        }
    }
}
