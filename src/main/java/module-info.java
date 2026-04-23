module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.google.gson;

    opens com.example to javafx.fxml;
    opens com.example.controller to javafx.fxml;
    opens com.example.model to com.google.gson;
    opens com.example.adapter to com.google.gson;

    exports com.example;
}
