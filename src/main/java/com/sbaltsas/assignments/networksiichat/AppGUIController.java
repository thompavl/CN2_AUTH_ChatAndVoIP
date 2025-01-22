package com.sbaltsas.assignments.networksiichat;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class AppGUIController {

    private boolean clientConnected = false;
    private boolean firstMessage = true;
    private Text placeHolder;

    @FXML
    private Button sendBtn;
    @FXML
    private ToggleButton callButton;
    @FXML
    private Label statusLabel;
    @FXML
    private ScrollPane textcontainer;
    @FXML
    private TextFlow messages;
    @FXML
    private TextField msgToSend;
    @FXML
    private TextField addressField;
    @FXML
    private Spinner<Integer> portField;

    @FXML
    public void initialize(){
        portField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,65535,6000));
        addressField.setText("localhost");
        System.out.println("gui initialized!");
        messages.getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                //messages.layout();
                textcontainer.layout();
                textcontainer.setVvalue(1.0); //scroll down
            }
        });
        placeHolder = new Text("No messages? Better start chatting!");
        messages.getChildren().add(placeHolder);
        statusLabel.setText(App.connectionState.toString());
    }

    @FXML
    protected void onMessageKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            System.out.println("ENTER pressed!");
            sendMessageToClient(msgToSend.getText());
        }
        keyEvent.consume();
    }

    @FXML
    protected void sendMessageClicked(ActionEvent actionEvent) {
        sendMessageToClient(msgToSend.getText());
        actionEvent.consume();
    }

    @FXML
    protected void connectButtonClick(ActionEvent actionEvent){
        int port = portField.getValue();
        try{
            InetAddress address = InetAddress.getByName(addressField.getText());
            App.startClient(address,port);
            clientConnected = true;
        }
        catch (UnknownHostException e){
            System.err.println("Peer address is invalid!");
            showError("Invalid Host","The peer address you entered is invalid!");
        }
        App.connectionState = ConnectionState.CONNECTED;
        statusLabel.setText(App.connectionState.toString());
        actionEvent.consume();
    }

    @FXML
    protected void callButtonClick(ActionEvent actionEvent){
        boolean call = callButton.isSelected();
        if (call){
            App.connectionState = ConnectionState.ON_CALL;
            callButton.setText("End call");
        }
        else{
            App.connectionState = ConnectionState.CONNECTED;
            callButton.setText("Call!");
        }
        if(clientConnected){
            App.callInput.isOnCall = call;
            statusLabel.setText(App.connectionState.toString());
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Not connected");
            alert.setHeaderText(null);
            alert.setContentText("You must connect to peer first in order to call!");
            alert.show();
        }
        actionEvent.consume();
    }

    @FXML
    protected synchronized void addMessage(String address,String message){
        if(firstMessage){
            firstMessage = false;
            messages.getChildren().remove(placeHolder);
        }
        Text text = new Text("<"+address+"> "+message+"\n");
        if(address.equalsIgnoreCase("you")){
            text.setFill(Color.GREEN);
            msgToSend.clear();
        }
        else{
            text.setFill(Color.BLUE);
        }
        messages.getChildren().add(text);
        String audioFile = Objects.requireNonNull(getClass().getResource("kururin.mp3")).toExternalForm();
        Media audio = new Media(audioFile);
        final MediaPlayer mediaPlayer = new MediaPlayer(audio);
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.stop();
        });
        mediaPlayer.play();
    }

    private synchronized void sendMessageToClient(String message){
        if(clientConnected){
            if(!message.isEmpty()){
                App.client.sendTextPacket(message);
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Not connected");
            alert.setHeaderText(null);
            alert.setContentText("You must connect to peer first in order to send messages!");
            alert.show();
        }
    }

    protected synchronized void showError(String title, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}