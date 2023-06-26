package software.ujithamigara.groupchatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.layout.AnchorPane;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LoginController {

    @FXML
    private AnchorPane node;

    @FXML
    private JFXTextField txtFieldUserName;

    @FXML
    void txtFieldUserNameOnAction(ActionEvent event) {

    }
    @FXML
    void loginOnAction(ActionEvent event) {

    }
    public void initialize(){
        new Thread(()-> {try {
            ServerSocket serverSocket = new ServerSocket(3002);
            Socket socket = serverSocket.accept();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());




        } catch (IOException e) {
            e.printStackTrace();
        }}).start();
    }
}