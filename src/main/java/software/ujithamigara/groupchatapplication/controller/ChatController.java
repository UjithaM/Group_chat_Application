package software.ujithamigara.groupchatapplication.controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatController {
    @FXML
    private AnchorPane node;
    @FXML
    private VBox Vbox;

    @FXML
    private JFXTextField txtFieldMassage;
    Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;

    @FXML
    void sendBtnOnAction(ActionEvent event) {
        try {
            dataOutputStream.writeUTF(txtFieldMassage.getText());
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void txtFiledMassageOnAction(ActionEvent event) {
        sendBtnOnAction(new ActionEvent());
    }
    public void initialize(){
            try {
                socket = new Socket("localhost", 3004);
                dataInputStream =  new  DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                new Thread(()->{
                    while (true) {
                        try {
                            String massage = dataInputStream.readUTF();
                            Platform.runLater(() -> {
                                Label label = new Label(massage);
                                Vbox.getChildren().add(label);
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }

}
