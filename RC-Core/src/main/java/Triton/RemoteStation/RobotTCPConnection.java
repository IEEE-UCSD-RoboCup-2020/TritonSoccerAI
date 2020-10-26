package Triton.RemoteStation;

import java.net.*;

import Proto.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;
import Proto.RemoteAPI.RemoteGeometry;
import Triton.DesignPattern.PubSubSystem.Module;
import Triton.DesignPattern.PubSubSystem.*;

import java.io.*;

public class RobotTCPConnection implements Module {
    private String ip;
    private int port;

    private Socket clientSocket;
    private DataOutputStream out;
    private BufferedReader in;

    private Subscriber<SSL_GeometryFieldSize> fieldSizeSub;
    private boolean isConnected;

    public RobotTCPConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
        fieldSizeSub = new FieldSubscriber<SSL_GeometryFieldSize>("geometry", "fieldSize");
    }

    public boolean connect() {
        try {
            clientSocket = new Socket(ip, port);
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            


            String line = in.readLine();
            if (line.equals("CONNECTION ESTABLISHED")){    
                isConnected = true;
                return true;
            }
            return false;
        } catch (UnknownHostException e) {
            isConnected = false;
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean sendGeometry() {
        fieldSizeSub.subscribe();

        SSL_GeometryFieldSize fieldSize = null;
        do {
            fieldSize = fieldSizeSub.getMsg();
        }
        while (fieldSize == null);

        RemoteGeometry.Builder toSend = RemoteGeometry.newBuilder();
        toSend.setFieldLength(fieldSize.getFieldLength());
        toSend.setFieldWidth(fieldSize.getFieldWidth());
        toSend.setGoalDepth(fieldSize.getGoalDepth());
        toSend.setGoalWidth(fieldSize.getGoalWidth());

        // byte[] geoByteArray = toSend.build().toByteArray();

        try {
            /// out.write(geoByteArray);
            out.writeChars("fhneowhfoiweho\n");
            String line = in.readLine();
            if (line.equals("GEOMETRY RECEIVED"))
                return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public void run() {
    }
}