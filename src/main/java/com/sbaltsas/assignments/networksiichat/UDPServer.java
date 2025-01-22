package com.sbaltsas.assignments.networksiichat;

import javafx.application.Platform;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UDPServer extends Thread{
    private DatagramSocket socket;
    private final AppGUIController appGUIController;
    public final static int PACKET_SIZE = 1500;
    private final byte[] buffer;

    public UDPServer(int port, AppGUIController guiController){
        buffer = new byte[PACKET_SIZE];
        appGUIController = guiController;
        try{
            socket = new DatagramSocket(null);
            InetSocketAddress address = new InetSocketAddress("0.0.0.0", port);
            socket.bind(address);
            System.out.println("[SERVER] UDP server started!");
        } catch (SocketException e){
            System.err.printf("[SERVER] UDP server could not be started! Reason %s", e);
            Platform.runLater(() -> {
                guiController.showError("UDP server startup failed",String.format("UDP server unable to start! Reason: %s",e));
            });
        }
    }

    @Override
    public void run(){
        while (!Thread.interrupted()) {
            try {
                Arrays.fill(buffer,(byte)0);
                DatagramPacket packet = new DatagramPacket(buffer, PACKET_SIZE);
                socket.receive(packet);
                System.out.printf("[SERVER] Got packet with length: %d, offset: %d\n",packet.getLength(),packet.getOffset());
                byte[] header = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getOffset() + 5); // split incoming message into header and payload
                byte[] payload = Arrays.copyOfRange(packet.getData(), packet.getOffset() + 5, packet.getLength());
                String headerText = new String(header, StandardCharsets.UTF_8);
                System.out.printf("Packet hdr: %s\n", headerText);
                if(headerText.equals("TEXT_")) {
                    String payloadText = new String(payload, StandardCharsets.UTF_8);
                    System.out.printf("Text message received! %s\n", payloadText);
                    Platform.runLater(() -> {
                        appGUIController.addMessage(packet.getAddress().getHostAddress(), payloadText);
                    });
                } else if (headerText.equals("CALL_") && App.callOutput != null) {
                    App.callOutput.loadBuffer(payload);
                }
            } catch (IOException e) {
                System.err.println("[SERVER] UDP packet could not be received!");
            }
        }
        socket.close();
    }
}
