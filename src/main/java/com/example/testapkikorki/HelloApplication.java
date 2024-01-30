package com.example.testapkikorki;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/mainPage.fxml"));

        Scene primaryStage = new Scene(fxmlLoader.load());

        InputStream inputStream = HelloApplication.class.getResourceAsStream("/png/logo.png");
        if (inputStream != null) {
            Image logo = new Image(inputStream);
            stage.getIcons().add(logo);
        }

        stage.initStyle(StageStyle.TRANSPARENT);

        stage.setScene(primaryStage);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}