package com.github.avereshchagin.ciscolab;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.TooManyListenersException;

public class ConnectionHandler implements Runnable, SerialPortEventListener {

    private static final int PORT_OPEN_TIMEOUT = 2000;
    private static final int PORT_SPEED = 9600;
    private static final int BUFFER_SIZE = 10240;

    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;
    private InputStream portIn;

    public ConnectionHandler(Socket socket) throws IOException {
        System.out.println("Accepted connection from " + socket.getInetAddress());
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
    }

    private SerialPort initPort(String portName) {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            if (!portIdentifier.isCurrentlyOwned()) {
                CommPort commPort = portIdentifier.open(getClass().getName(), PORT_OPEN_TIMEOUT);
                if (commPort instanceof SerialPort) {
                    SerialPort serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(PORT_SPEED, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    return serialPort;
                }
            }
        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException e) {
            e.printStackTrace();
        }
        return null;
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
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        SerialPort port = initPort("/dev/ttyS0");
        if (port == null) {
            try {
                out.write("Unable to open console port.\n".getBytes());
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
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
                e.printStackTrace();
            }

            // Closing the port
            port.removeEventListener();
            portOut.close();
            portIn.close();
            PortCloser.close(port);
        } catch (IOException | TooManyListenersException e) {
            e.printStackTrace();
        }
    }
}
