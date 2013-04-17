package com.github.avereshchagin.ciscolab;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamProxy implements Runnable {

    private final InputStream in;
    private final OutputStream out;

    public StreamProxy(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        int symbol;
        try {
            while (-1 != (symbol = in.read())) {
                if (symbol == '\n') {
                    out.write('\r');
                }
                out.write(symbol);
                out.flush();

                System.out.write(symbol);
                System.out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}