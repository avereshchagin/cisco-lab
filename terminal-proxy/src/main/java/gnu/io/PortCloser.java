package gnu.io;

public class PortCloser {

    private PortCloser() {

    }

    public static void close(SerialPort port) {
        if (port instanceof RXTXPort) {
            RXTXPort rxtxPort = (RXTXPort) port;
            rxtxPort.IOLocked = 0;
        }
        port.close();
    }
}
