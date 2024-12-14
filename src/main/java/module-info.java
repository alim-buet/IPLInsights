module com.example.iplinsights {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    opens controllers to javafx.fxml;
    opens dataModels to javafx.fxml;
    opens network to javafx.fxml;
    opens server to javafx.fxml;
    exports controllers;
    exports dataModels;
    exports network;
    exports server;
    exports client;
//    opens com.example.iplinsights to javafx.fxml;
//    exports com.example.iplinsights;
}
