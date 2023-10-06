import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

public class index {
    public static void main(String[] args) {
        try {
            String portName = "COM1"; // Change to your serial port name
            int baudRate = 9600; // Change to your baud rate

            // List available serial ports
            listSerialPorts();

            // Open and configure the serial port
            SerialPort serialPort = openSerialPort(portName, baudRate);
            if (serialPort != null) {
                InputStream inputStream = serialPort.getInputStream();
                serialPort.addEventListener(new SerialReader(inputStream));
                serialPort.notifyOnDataAvailable(true);
            } else {
                System.err.println("Failed to open the serial port.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listSerialPorts() {
        CommPortIdentifier portIdentifier;
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            portIdentifier = portList.nextElement();
            System.out.println("Serial Port: " + portIdentifier.getName());
        }
    }

    public static SerialPort openSerialPort(String portName, int baudRate) throws Exception {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.err.println("Error: Port is currently in use");
            return null;
        } else {
            SerialPort serialPort = (SerialPort) portIdentifier.open("index", 2000);
            serialPort.setSerialPortParams(
                baudRate,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE
            );
            return serialPort;
        }
    }

    public static class SerialReader implements SerialPortEventListener {
        private InputStream inputStream;

        public SerialReader(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                try {
                    int availableBytes = inputStream.available();
                    byte[] serialData = new byte[availableBytes];
                    inputStream.read(serialData);
                    String data = new String(serialData);
                    System.out.println("Received data: " + data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
