package software.ujithamigara.groupchatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import software.ujithamigara.groupchatapplication.Launcher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LoginController {

    @FXML
    private AnchorPane node;

    @FXML
    private JFXTextField txtFieldUserName;
    ServerSocket serverSocket;

    List<Socket> socketList = new ArrayList<>();

    @FXML
    void txtFieldUserNameOnAction(ActionEvent event) {

    }

    @FXML
    void loginOnAction(ActionEvent event) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("/software/ujithamigara/groupchatapplication/Chat.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            stage.setTitle(txtFieldUserName.getText() + " chat");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(3004);
                while (true) {
                    Socket socket = serverSocket.accept();
                    socketList.add(socket);
                    startClientThread(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startClientThread(Socket clientSocket) {
        new Thread(() -> {
            try {
                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());

                while (true) {
                    String message = dataInputStream.readUTF();
                    if (message != null) {
                        broadcastMessage(message);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void broadcastMessage(String message) {
        for (Socket client : socketList) {
            try {
                DataOutputStream clientOutputStream = new DataOutputStream(client.getOutputStream());
                clientOutputStream.writeUTF(txtFieldUserName.getText()+" : "+message);
                clientOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
