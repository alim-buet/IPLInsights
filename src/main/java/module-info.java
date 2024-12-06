module com.example.iplinsights {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.iplinsights to javafx.fxml;
    exports com.example.iplinsights;
}