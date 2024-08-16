package com.yourdomain;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends Application {
    private final Map<String, String> users = new HashMap<>(); // Stores users in memory

    @Override
    public void start(Stage primaryStage) {
        VBox loginPane = createLoginPane(primaryStage);
        Scene scene = new Scene(loginPane, 300, 250); // Increased height to accommodate checkbox
        primaryStage.setScene(scene);
        primaryStage.setTitle("GoGood - Login");
        primaryStage.show();
    }

    public VBox createLoginPane(Stage primaryStage) {
        VBox vbox = new VBox();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("user@example.com");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        TextField showPasswordField = new TextField();
        showPasswordField.setPromptText("Enter your password");
        showPasswordField.setVisible(false); // Initially hidden

        // Bind the two fields so they always have the same text
        showPasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        CheckBox showPasswordCheckbox = new CheckBox("Show Password");
        showPasswordCheckbox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                showPasswordField.setVisible(true);
                passwordField.setVisible(false);
            } else {
                showPasswordField.setVisible(false);
                passwordField.setVisible(true);
            }
        });

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            if (authenticate(email, password)) {
                System.out.println("Login succeeded! Welcome " + email);
                GoGoodAppUI.unlockTabs(); // Unlock the rest of the system
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password.");
            }
        });

        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> showSignUpDialog(primaryStage));

        vbox.getChildren().addAll(emailLabel, emailField, passwordLabel, passwordField, showPasswordField, showPasswordCheckbox, loginButton, signUpButton);
        return vbox;
    }

    private boolean authenticate(String email, String password) {
        return users.containsKey(email) && users.get(email).equals(password);
    }

    private void showSignUpDialog(Stage primaryStage) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Sign Up");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField("+1");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        VBox vbox = new VBox(nameLabel, nameField, emailLabel, emailField, phoneLabel, phoneField, passwordLabel, passwordField);
        dialog.getDialogPane().setContent(vbox);

        ButtonType signUpButtonType = new ButtonType("Sign Up", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(signUpButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == signUpButtonType) {
                String email = emailField.getText();
                String password = passwordField.getText();
                users.put(email, password); // Save user information in memory
                System.out.println("Sign-up successful for " + email);
            }
            return null;
        });

        dialog.initOwner(primaryStage);
        dialog.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
