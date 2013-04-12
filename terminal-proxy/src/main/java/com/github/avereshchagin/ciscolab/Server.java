package com.github.avereshchagin.ciscolab;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final int SERVER_PORT = 8338;

    private final ServerSocket serverSocket;

    public Server() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
    }

    public void loop() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                int data;
                while (-1 != (data = in.read())) {
                    out.write(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
