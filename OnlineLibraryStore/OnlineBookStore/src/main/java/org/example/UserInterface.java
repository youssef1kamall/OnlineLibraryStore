package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.ui.UserUI;

public class UserInterface extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("User Management System");

        // Delegate UI initialization to UserUI
        UserUI userUI = new UserUI(primaryStage);
        userUI.loadMainUI(); // Load login/signup UI

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
