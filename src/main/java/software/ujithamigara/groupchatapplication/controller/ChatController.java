package software.ujithamigara.groupchatapplication.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

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
    String updated = "";
    String userName;

    @FXML
    void sendBtnOnAction(ActionEvent event) {
        Stage stage = (Stage) node.getScene().getWindow();
        userName = stage.getTitle();
        String massage = txtFieldMassage.getText();
        try {
            dataOutputStream.writeUTF("TEXT");
            dataOutputStream.writeUTF(userName +"\n"+massage);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updated = "done";
        txtFieldMassage.setText("");
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
                                if (updated.equals("done")) {
                                    Label label = new Label(message);
                                    label.setStyle("-fx-font-size: 20px; -fx-padding: 20px;");
                                    label.setBackground(new Background(new BackgroundFill(Color.web("#25D366"), new CornerRadii(10), new Insets(10))));
                                    BorderPane borderPane = new BorderPane();
                                    borderPane.setRight(label);
                                    vBox.getChildren().add(borderPane);
                                    updated = "";
                                }else {
                                    Label label = new Label(message);
                                    label.setStyle("-fx-font-size: 20px; -fx-padding: 20px;");
                                    label.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), new Insets(10))));
                                    vBox.getChildren().add(label);
                                }
                            });
                        } else if (messageType.equals("IMAGE")) {
                            String user = dataInputStream.readUTF();
                            // Image message
                            int fileSize = dataInputStream.readInt();
                            byte[] fileData = new byte[fileSize];
                            dataInputStream.readFully(fileData);
                            Platform.runLater(() -> {
                                // Create an ImageView to display the received image
                                ImageView imageView = new ImageView();
                                imageView.setPreserveRatio(true);
                                imageView.setFitWidth(100); // Adjust the width as needed
                                imageView.setFitHeight(100); // Adjust the height as needed

                                // Load and set the image
                                try {
                                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileData);
                                    Image image = new Image(byteArrayInputStream);
                                    imageView.setImage(image);
                                    if (updated.equals("done")) {
                                        Label label = new Label(user);
                                        label.setStyle("-fx-font-size: 20px; -fx-padding: 20px;");
                                        label.setBackground(new Background(new BackgroundFill(Color.web("#25D366"), new CornerRadii(10), new Insets(10))));
                                        BorderPane borderPane1 = new BorderPane();
                                        borderPane1.setRight(label);

                                        BorderPane borderPane = new BorderPane();
                                        borderPane.setRight(imageView);
                                        vBox.getChildren().add(borderPane1);
                                        vBox.getChildren().add(borderPane);
                                        updated = "";
                                    }else {
                                        Label label = new Label(user);
                                        label.setStyle("-fx-font-size: 20px; -fx-padding: 20px;");
                                        label.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), new Insets(10))));
                                        vBox.getChildren().add(label);
                                        vBox.getChildren().add(imageView);
                                    }
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
        Stage stage = (Stage) node.getScene().getWindow();
        userName = stage.getTitle();
        updated = "done";
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
                    dataOutputStream.writeUTF(userName);
                    dataOutputStream.writeInt(fileData.length);
                    dataOutputStream.write(fileData);
                    dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @FXML
    void emojiBtnOnAction(ActionEvent event) {
        updated = "done";
        txtFieldMassage.requestFocus();

        // Delay execution for a short time to ensure TextField has focus
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_WINDOWS);
            robot.keyPress(KeyEvent.VK_PERIOD);
            robot.keyRelease(KeyEvent.VK_PERIOD);
            robot.keyRelease(KeyEvent.VK_WINDOWS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
