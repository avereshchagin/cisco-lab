package com.github.avereshchagin.ciscolab.proxy;

import com.github.avereshchagin.ciscolab.util.PortDispatcher;
import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionHandler implements Runnable, SerialPortEventListener {

    private static final int BUFFER_SIZE = 10240;

    private static final Logger LOGGER = Logger.getLogger(ConnectionHandler.class.getName());

    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;
    private InputStream portIn;

    public ConnectionHandler(Socket socket) throws IOException {
        LOGGER.info("Accepted connection from " + socket.getInetAddress());
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        byte[] data = new byte[BUFFER_SIZE];
        try {
            int len = portIn.read(data);
            while (len != -1) {
                out.write(data, 0, len);
                out.flush();
                len = portIn.read(data);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "serialEvent", e);
        }
    }

    @Override
    public void run() {
        final String name = "/dev/ttyS0";
        SerialPort port = PortDispatcher.INSTANCE.requestPort(name);
        if (port == null) {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "run", e);
            }
            return;
        }
        try {
            portIn = port.getInputStream();
            OutputStream portOut;
            portOut = port.getOutputStream();

            port.addEventListener(this);
            port.notifyOnDataAvailable(true);

            // Initializing the terminal
            portOut.write("\r".getBytes());
            portOut.flush();

            // Receiving data from remote side loop
            int symbol;
            try {
                while (-1 != (symbol = in.read())) {
                    portOut.write(symbol);
                    portOut.flush();
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "run", e);
            }

            // Closing the port
            port.removeEventListener();
            portOut.close();
            portIn.close();
            out.close();
            in.close();
        } catch (IOException | TooManyListenersException e) {
            LOGGER.log(Level.SEVERE, "run", e);
        } finally {
            PortDispatcher.INSTANCE.closePort(name);
        }
    }
}
