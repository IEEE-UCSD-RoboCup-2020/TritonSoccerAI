//package Triton.VirtualBot.SimulatorDependent.GrSim_OldProto;
//
//import Triton.Config.GlobalVariblesAndConstants.GvcGeneral;
//import Triton.Legacy.OldGrSimProto.protosrcs.GrSimCommands;
//import Triton.Legacy.OldGrSimProto.protosrcs.GrSimPacket;
//import Triton.Misc.ModulePubSubSystem.FieldSubscriber;
//import Triton.Misc.ModulePubSubSystem.Module;
//import Triton.Misc.ModulePubSubSystem.Subscriber;
//
//import java.io.IOException;
//import java.net.*;
//import java.util.ArrayList;
//
//public class GrSimClient implements Module {
//    protected InetAddress address;
//    protected DatagramSocket socket;
//
//    protected int port;
//    protected int ID;
//
//    private final ArrayList<Subscriber<GrSimCommands.grSim_Robot_Command>> grSimBotCmdSubs;
//
//    private boolean isFirstRun = true;
//
//    public GrSimClient(String ip, int port) {
//        this.port = port;
//        this.ID = ID;
//
//        try {
//            socket = new DatagramSocket();
//            address = InetAddress.getByName(ip);
//        } catch (SocketException | UnknownHostException e) {
//            e.printStackTrace();
//        }
//
//        grSimBotCmdSubs = new ArrayList<Subscriber<GrSimCommands.grSim_Robot_Command>>();
//        for (int i = 0; i < GvcGeneral.numAllyRobots; i++) {
//            grSimBotCmdSubs.add(new FieldSubscriber<GrSimCommands.grSim_Robot_Command>(
//                    "grSim", "botCmd" + i));
//        }
//    }
//
//    @Override
//    public void run() {
//        if (isFirstRun) {
//            subscribe();
//            isFirstRun = false;
//        }
//
//        sendData();
//    }
//
//    /**
//     * Subscribe to publishers
//     */
//    private void subscribe() {
//        try {
//            for (Subscriber<GrSimCommands.grSim_Robot_Command> grSimBotCmdSub : grSimBotCmdSubs) {
//                grSimBotCmdSub.subscribe(1000);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendData() {
//        GrSimPacket.grSim_Packet.Builder grSimPacket = GrSimPacket.grSim_Packet.newBuilder();
//        GrSimCommands.grSim_Commands.Builder grSimCommands = GrSimCommands.grSim_Commands.newBuilder();
//
//        for (int i = 0; i < GvcGeneral.numAllyRobots; i++) {
//            grSimCommands.setRobotCommands(i, grSimBotCmdSubs.get(i).getMsg());
//        }
//        grSimPacket.setCommands(grSimCommands);
//
//        byte[] bytes;
//        bytes = grSimPacket.build().toByteArray();
//        send(bytes);
//    }
//
//    protected void send(byte[] msg) {
//        DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);
//        try {
//            socket.send(packet);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}