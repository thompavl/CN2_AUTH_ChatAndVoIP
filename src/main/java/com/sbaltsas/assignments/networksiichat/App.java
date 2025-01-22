package com.sbaltsas.assignments.networksiichat;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.InetAddress;

public class App extends Application {
    protected static int PORT = 6000;
    protected static UDPClient client = null;
    protected static CallInput callInput = null;
    protected static CallOutput callOutput = null;
    protected static ConnectionState connectionState = null;
    private static UDPServer server = null;
    private static AppGUIController guiController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("AppGUI2.fxml"));
        connectionState = ConnectionState.READY;
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("BingChat");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if(server != null){
                    server.interrupt();
                }
                if(client != null){
                    client.interrupt();
                }
                System.exit(0);
            }
        });
        stage.show();
        System.out.println("stage set!");
        guiController = fxmlLoader.getController();
        startServer(App.PORT);
        startAudio();
    }

    public static void main(String[] args) {
        System.out.printf("App will listen to port %d for connections!%n",PORT);
        launch();
    }

    private static void startServer(int port){
        server = new UDPServer(port, guiController);
        server.start();

    }
    private static void startAudio(){
        callInput = new CallInput();
        callInput.start();
        callOutput = new CallOutput();
        callOutput.start();
    }
    protected static void startClient(InetAddress address, int port){
        client = new UDPClient(address,port,guiController);
        client.start();

    }
}