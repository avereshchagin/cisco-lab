package com.github.avereshchagin.ciscolab;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.TooManyListenersException;

public class ConnectionHandler implements Runnable {

    private static final int PORT_OPEN_TIMEOUT = 2000;
    private static final int PORT_SPEED = 9600;

    private final Socket socket;

    public ConnectionHandler(Socket socket) {
        System.out.println("Accepted connection from " + socket.getInetAddress());
        this.socket = socket;
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

//    public static class SerialReader implements SerialPortEventListener {
//        private InputStream in;
//        private byte[] buffer = new byte[1024];
//
//        public SerialReader(InputStream in) {
//            this.in = in;
//        }
//
//        public void serialEvent(SerialPortEvent arg0) {
//            int data;
//
//            try {
//                int len = 0;
//                while ((data = in.read()) > -1) {
//                    if (data == '\n') {
//                        break;
//                    }
//                    buffer[len++] = (byte) data;
//                }
//                System.out.print(new String(buffer, 0, len));
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.exit(-1);
//            }
//        }
//
//    }

    @Override
    public void run() {
        SerialPort port = initPort("/dev/ttyS0");
        if (port == null) {
            return;
        }
        try {
            InputStream portIn = port.getInputStream();
            OutputStream portOut = port.getOutputStream();

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

//            port.addEventListener(new SerialReader(portIn));
//            port.notifyOnDataAvailable(true);
            Thread portReader = new Thread(new StreamProxy(portIn, out));
            Thread portWriter = new Thread(new StreamProxy(in, portOut));
            portReader.start();
            portWriter.start();

            portOut.write("\r\n".getBytes());
            portOut.flush();

            try {
                portReader.join();
                portWriter.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
//        } catch (TooManyListenersException e) {
//            e.printStackTrace();
        }
    }
}
