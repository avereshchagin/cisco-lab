package com.github.avereshchagin.ciscolab.util;

import gnu.io.PortCloser;
import gnu.io.SerialPort;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PortDispatcher {

    public static final PortDispatcher INSTANCE = new PortDispatcher();

    private static final Logger LOGGER = Logger.getLogger(PortDispatcher.class.getName());

    private Map<String, SerialPort> nameToPort = new HashMap<>();

    private PortDispatcher() {
    }

    public synchronized SerialPort requestPort(String name) {
        LOGGER.info("requestPort: " + name);
        SerialPort port = CommPortUtils.initPort(name);
        if (port != null) {
            nameToPort.put(name, port);
        }
        return port;
    }

    public synchronized void closePort(String name) {
        LOGGER.info("closePort: " + name);
        if (nameToPort.containsKey(name)) {
            SerialPort port = nameToPort.get(name);
            if (port != null) {
                PortCloser.close(port);
                nameToPort.remove(name);
                LOGGER.info("Port closed: " + name);
            }
        }
    }
}
