package com.sbaltsas.assignments.networksiichat;

import javafx.application.Platform;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UDPClient extends Thread{
    private DatagramSocket socket;
    private final AppGUIController appGUIController;
    private final int PEER_PORT;
    private final InetAddress PEER_ADDRESS;
    private final BlockingQueue<byte[]> messageQueue;

    public UDPClient(InetAddress address, int port, AppGUIController guiController){
        PEER_ADDRESS = address;
        PEER_PORT = port;
        appGUIController = guiController;
        messageQueue = new LinkedBlockingQueue<byte[]>();
        try{
            socket = new DatagramSocket();
        }
        catch (SocketException e){
            System.err.println("[CLIENT] Error! Cannot initialize UDP client!");
            Platform.runLater(() -> {
                appGUIController.showError("Peer connection failed","Cannot connect to peer! Is it online?");
            });
        }
    }

    @Override
    public void run(){
        while(!Thread.interrupted()) {
            try {
                final byte[] finalMessage = messageQueue.take();
                DatagramPacket packet = new DatagramPacket(finalMessage, finalMessage.length, PEER_ADDRESS, PEER_PORT);
                socket.send(packet);
                Platform.runLater(() -> {
                    byte[] header = Arrays.copyOfRange(finalMessage, 0, 5);
                    byte[] payload = Arrays.copyOfRange(finalMessage, 5, finalMessage.length);
                    if(Arrays.equals(header, "TEXT_".getBytes())){
                        appGUIController.addMessage("You",new String(payload, StandardCharsets.UTF_8));
                    }
                });
            } catch (IOException e) {
                System.err.println("[CLIENT] Packet could not be sent!");
            }
            catch (InterruptedException e) {
                messageQueue.clear();
            }
        }
        socket.close();
    }
    private void addMessageToQueue(byte[] message){
        //String messageToAdd = new String(message.getBytes(), StandardCharsets.UTF_8);
        messageQueue.add(message);
    }
    protected void sendTextPacket(String text){
        String payloadString = "TEXT_".concat(text);
        addMessageToQueue(payloadString.getBytes());
    }
    protected void sendVoicePacket(byte[] data){
        byte[] packetData = new byte[5 + data.length];
        System.arraycopy("CALL_".getBytes(), 0, packetData, 0, 5);
        System.arraycopy(data, 0, packetData, 5, data.length);
        addMessageToQueue(packetData);
    }
}
