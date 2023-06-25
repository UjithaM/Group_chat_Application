module software.ujithamigara.groupchatapplication {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens software.ujithamigara.groupchatapplication to javafx.fxml;
    exports software.ujithamigara.groupchatapplication;
    exports software.ujithamigara.groupchatapplication.controller;
    opens software.ujithamigara.groupchatapplication.controller to javafx.fxml;
}