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
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("/software/ujithamigara/groupchatapplication/Chat.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            stage.setTitle(txtFieldUserName.getText()+" chat");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void initialize(){
        new Thread(()-> {try {
            ServerSocket serverSocket = new ServerSocket(3004);
            Socket socket = serverSocket.accept();

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());




        } catch (IOException e) {
            e.printStackTrace();
        }}).start();
    }
}