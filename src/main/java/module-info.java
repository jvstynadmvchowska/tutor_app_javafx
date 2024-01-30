module com.example.testapkikorki {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.testapkikorki to javafx.fxml;
    exports com.example.testapkikorki;
}