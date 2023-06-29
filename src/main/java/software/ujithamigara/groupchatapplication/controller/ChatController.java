package software.ujithamigara.groupchatapplication.controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class ChatController {
    @FXML
    private AnchorPane node;
    @FXML
    private VBox vBox;


    @FXML
    private JFXTextField txtFieldMassage;
    Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;

    @FXML
    void sendBtnOnAction(ActionEvent event) {
        try {
            dataOutputStream.writeUTF("TEXT");
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
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        // Read the message type
                        String messageType = dataInputStream.readUTF();

                        if (messageType.equals("TEXT")) {
                            // Text message
                            String message = dataInputStream.readUTF();
                            Platform.runLater(() -> {
                                Label label = new Label(message);
                                vBox.getChildren().add(label);
                            });
                        } else if (messageType.equals("IMAGE")) {
                            // Image message
                            int fileSize = dataInputStream.readInt();
                            byte[] fileData = new byte[fileSize];
                            dataInputStream.readFully(fileData);
                            Platform.runLater(() -> {
                                // Create an ImageView to display the received image
                                ImageView imageView = new ImageView();
                                imageView.setPreserveRatio(true);
                                imageView.setFitWidth(50); // Adjust the width as needed
                                imageView.setFitHeight(50); // Adjust the height as needed

                                // Load and set the image
                                try {
                                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileData);
                                    Image image = new Image(byteArrayInputStream);
                                    imageView.setImage(image);
                                    vBox.getChildren().add(imageView);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void cameraOnAction(ActionEvent event) {
        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                    new FileChooser.ExtensionFilter("JPEG Files", "*.jpg")
            );

            // Show file chooser dialog
            File selectedFile = fileChooser.showOpenDialog(null);

            // Process selected file
            if (selectedFile != null) {
                try {
                    // Read the image file as bytes
                    byte[] fileData = Files.readAllBytes(selectedFile.toPath());

                    // Send the image file to the server
                    dataOutputStream.writeUTF("IMAGE");
                    dataOutputStream.writeInt(fileData.length);
                    dataOutputStream.write(fileData);
                    dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
