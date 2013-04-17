package com.github.avereshchagin.ciscolab;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int SERVER_PORT = 8338;
    private static final int NUMBER_OF_THREADS = 12;

    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    public Server() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        pool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    }

    public void loop() {
        try {
            while (true) {
                pool.execute(new ConnectionHandler(serverSocket.accept()));
            }
        } catch (IOException e) {
            pool.shutdown();
            e.printStackTrace();
        }
    }
}
