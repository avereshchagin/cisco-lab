package com.github.avereshchagin.ciscolab;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("91.238.230.93", 8338);
        InputStream remoteIn = socket.getInputStream();
        OutputStream remoteOut = socket.getOutputStream();

        Thread inputThread = new Thread(new InputReceiver(System.in, remoteOut));
        inputThread.setName("Input Proxy");
        Thread outputThread = new Thread(new OutputProxy(remoteIn, System.out));
        outputThread.setName("Output Proxy");
        inputThread.start();
        outputThread.start();
    }

}
