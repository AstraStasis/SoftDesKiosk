module com.example.softdeskiosk {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.softdeskiosk to javafx.fxml;
    exports com.example.softdeskiosk;
}