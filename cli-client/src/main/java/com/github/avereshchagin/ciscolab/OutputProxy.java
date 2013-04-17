package com.github.avereshchagin.ciscolab;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OutputProxy implements Runnable {

    private static final int SLEEP_TIME = 100;
    private static final int BUFFER_SIZE = 10240;

    private final InputStream in;
    private final OutputStream out;

    public OutputProxy(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        byte[] data = new byte[BUFFER_SIZE];
        try {
            while (true) {
                int available = in.available();
                if (available > 0) {
                    int len = available < BUFFER_SIZE ? available : BUFFER_SIZE;
                    len = in.read(data, 0, len);
                    if (len == -1) {
                        break;
                    }
                    out.write(data, 0, len);
                    out.flush();
                } else {
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
