package Triton.Modules.RemoteStation;

import Triton.Config.ObjectConfig;
import Triton.Dependencies.DesignPattern.PubSubSystem.Module;
import Triton.Dependencies.DesignPattern.PubSubSystem.*;
import Triton.Modules.Detection.RobotData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * TCP connection to robot
 */
public class RobotTCPConnection implements Module {
    private final String ip;
    private final int port;
    private final int ID;
    private final Publisher<Boolean> dribStatPub;
    private final Subscriber<RobotData> allySub;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final Publisher<String> tcpCommandPub;
    private final Subscriber<String> tcpCommandSub;
    private final Publisher<Boolean> tcpInitPub;
    private boolean isConnected;

    private SendTCPRunnable sendTCP;
    private ReceiveTCPRunnable receiveTCP;

    private class SendTCPRunnable implements Runnable {
        private PrintWriter out;

        public SendTCPRunnable(PrintWriter out) {
            this.out = out;
        }

        private void subscribe() {
            try {
                tcpCommandSub.subscribe();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                subscribe();

                while (true) {
                    String msg = tcpCommandSub.getMsg();
                    System.out.println(msg);
                    out.println(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ReceiveTCPRunnable implements Runnable {
        private BufferedReader in;
        private int ID;

        public ReceiveTCPRunnable(BufferedReader in, int ID) {
            this.in = in;
            this.ID = ID;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String line = in.readLine();
//                    System.out.printf("Ally %d TCP : %s\n", ID, line);
                    if (!line.equals("bazinga")) {
                        System.out.printf("Ally %d TCP : %s\n", ID, line);
                    }

                    switch (line) {
                        case "BallOnHold" -> dribStatPub.publish(true);
                        case "BallOffHold" -> dribStatPub.publish(false);
                        case "Initialized" -> tcpInitPub.publish(true);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Constructs TCP connection
     *
     * @param ip   ip of connection
     * @param port to connect to
     * @param ID   ID of robot
     */
    public RobotTCPConnection(String ip, int port, int ID) {
        this.ip = ip;
        this.port = port;
        this.ID = ID;

        dribStatPub = new FieldPublisher<>("Ally drib", "" + ID, false);
        allySub = new FieldSubscriber<>("detection", ObjectConfig.MY_TEAM.name() + ID);
        tcpCommandPub = new MQPublisher<>("tcpCommand", "" + ID);
        tcpCommandSub = new MQSubscriber<>("tcpCommand", "" + ID);
        tcpInitPub = new FieldPublisher<>("tcpInit", "" + ID, true);
    }

    /**
     * Begin connection
     *
     * @return true if connection was successfully established
     */
    public boolean connect() throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String line = in.readLine();
        System.out.printf("Ally %d TCP : %s\n", ID, line);
        if (line.equals("CONNECTION ESTABLISHED")) {
            isConnected = true;

            sendTCP = new SendTCPRunnable(out);
            receiveTCP = new ReceiveTCPRunnable(in, ID);
            return true;
        }
        return false;
    }

    /**
     * Sends initial location to robot
     */
    public void sendInit() {
        if (!isConnected) {
            return;
        }

        subscribe();
        RobotData allyData = allySub.getMsg();

        String str = String.format("init %d %d", (int) allyData.getPos().x, (int) allyData.getPos().y);
        tcpCommandPub.publish(str);
    }

    @Override
    public void run() {
        try {
            while (true) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to publishers
     */
    private void subscribe() {
        try {
            allySub.subscribe(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns true if there is still a connection
     *
     * @return true if there is still a connection
     */
    public boolean isConnected() {
        return isConnected;
    }

    public SendTCPRunnable getSendTCP() {
        return sendTCP;
    }

    public ReceiveTCPRunnable getReceiveTCP() {
        return receiveTCP;
    }
}
