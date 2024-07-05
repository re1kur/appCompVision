package org.example;

import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class of interface of app.
 */
public class App extends Application {
    public static void main(String[] args) {
        launch();
    }

    /**
     * Creates the main window for start program
     * @param stage primary stage
     * @throws IOException will be if wrong path
     */
    public void start(Stage stage) throws IOException {
        Stage primaryStage = new Stage();
        Handler.setPrimaryStage(primaryStage);
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/view/app.fxml")));
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
