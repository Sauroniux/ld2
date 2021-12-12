module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.java;
    requires java.sql;

    requires spring.context;
    requires spring.web;
    requires gson;
    requires spring.core;


    opens com.example.ld1 to javafx.fxml;
    exports com.example.ld1;

    opens com.example.ld1.data to javafx.fxml, com.example.ld2;
    exports com.example.ld1.data;
    opens com.example.ld1.fxControllers to javafx.fxml;
    exports com.example.ld1.fxControllers;
}