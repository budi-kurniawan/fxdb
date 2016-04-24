package com.brainysoftware.fxdb.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main class
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class FxDb extends Application {
    public static void main(String[] args) {
        // create data directories
        Path dataPath = Paths.get("./data/saved-sql");
        if (!Files.exists(dataPath)) {
            try {
                Files.createDirectories(dataPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/styles.css");
        stage.setScene(scene);
        stage.show();
    }
}