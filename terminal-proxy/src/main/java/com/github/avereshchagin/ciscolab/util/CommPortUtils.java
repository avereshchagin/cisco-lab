package com.github.avereshchagin.ciscolab.util;

import gnu.io.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommPortUtils {

    private static final int PORT_OPEN_TIMEOUT = 2000;
    private static final int PORT_SPEED = 9600;

    private static final Logger LOGGER = Logger.getLogger(CommPortUtils.class.getName());

    private CommPortUtils() {
    }

    public static List<String> listAvailablePorts() {
        List<String> result = new ArrayList<>();
        @SuppressWarnings("unchecked")
        Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()) {
            CommPortIdentifier port = ports.nextElement();
            if (port.getPortType() == CommPortIdentifier.PORT_SERIAL && !port.isCurrentlyOwned()) {
                result.add(port.getName());
            }
        }
        return result;
    }

    public static SerialPort initPort(String name) {
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(name);
            if (!portIdentifier.isCurrentlyOwned()) {
                CommPort commPort = portIdentifier.open("terminal-proxy", PORT_OPEN_TIMEOUT);
                if (commPort instanceof SerialPort) {
                    SerialPort serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(PORT_SPEED, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    return serialPort;
                }
            }
        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException e) {
            LOGGER.log(Level.SEVERE, "initPort", e);
        }
        LOGGER.severe("Unable to init port: " + name);
        return null;
    }
}
