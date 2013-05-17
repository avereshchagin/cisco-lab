package com.github.avereshchagin.ciscolab.proxy;

import com.github.avereshchagin.ciscolab.ConnectionParameters;
import com.github.avereshchagin.ciscolab.util.PortDispatcher;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.*;
import java.net.Socket;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionHandler implements Runnable, SerialPortEventListener {

    private static final int BUFFER_SIZE = 10240;

    private static final Logger LOGGER = Logger.getLogger(ConnectionHandler.class.getName());

    private static final Gson GSON = new Gson();

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

    private String getPortName() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            ConnectionParameters parameters = GSON.fromJson(reader.readLine(), ConnectionParameters.class);
            if (parameters != null) {
                Client client = Client.create();
                WebResource resource = client.resource("http://192.168.30.5:8080/webapp/api/getPortName");
                String response = resource.
                        queryParam("accessToken", parameters.getAccessToken()).
                        queryParam("deviceId", String.valueOf(parameters.getDeviceId())).
                        get(String.class);
                String portName = GSON.fromJson(response, String.class);
                String result = "OK\n";
                if (portName == null || portName.isEmpty()) {
                    result = "FAIL\n";
                    portName = null;
                }
                out.write(result.getBytes());
                out.flush();
                return portName;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "getPortName", e);
        }
        return null;
    }

    @Override
    public void run() {
        SerialPort port = null;
        final String name = getPortName();
        if (name != null) {
            port = PortDispatcher.INSTANCE.requestPort(name);
        }
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
