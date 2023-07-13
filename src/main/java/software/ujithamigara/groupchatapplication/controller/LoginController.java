package software.ujithamigara.groupchatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import software.ujithamigara.groupchatapplication.Launcher;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LoginController {
    @FXML
    private JFXTextField txtFieldUserName;
    ServerSocket serverSocket;

    List<Socket> socketList = new ArrayList<>();

    @FXML
    void txtFieldUserNameOnAction(ActionEvent event) {
        loginOnAction(new ActionEvent());
    }

    @FXML
    void loginOnAction(ActionEvent event) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("/software/ujithamigara/groupchatapplication/Chat.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            stage.setTitle(txtFieldUserName.getText());
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
                    // Read the message type
                    String messageType = dataInputStream.readUTF();

                    if (messageType.equals("TEXT")) {
                        // Text message
                        String message = dataInputStream.readUTF();
                        if (message != null) {
                            broadcastMessage(message);
                        }
                    } else if (messageType.equals("IMAGE")) {
                        // Image message
                        String name = dataInputStream.readUTF();
                        int fileSize = dataInputStream.readInt();
                        byte[] fileData = new byte[fileSize];
                        dataInputStream.readFully(fileData);
                        if (fileData != null) {
                            broadcastImage(fileData, name);
                        }
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
                clientOutputStream.writeUTF("TEXT");
                clientOutputStream.writeUTF(message);
                clientOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void broadcastImage(byte[] fileData, String userName) {
        for (Socket client : socketList) {
            try {
                DataOutputStream clientOutputStream = new DataOutputStream(client.getOutputStream());
                clientOutputStream.writeUTF("IMAGE");
                clientOutputStream.writeUTF(userName);
                clientOutputStream.writeInt(fileData.length);
                clientOutputStream.write(fileData);
                clientOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
