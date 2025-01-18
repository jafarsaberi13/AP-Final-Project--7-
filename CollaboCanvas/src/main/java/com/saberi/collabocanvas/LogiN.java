package com.saberi.collabocanvas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;

public class logiN extends Application {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8888;

    /**
     * Creates the sign-up page layout.
     *
     * @param stage the primary stage for the application.
     * @return a {@code BorderPane} containing the sign-up page layout.
     */
    private BorderPane createSignUpPage(Stage stage) {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #87CEEB;");

        VBox Box = styledVBox();

        Label title = new Label("Sign Up");
        title.setFont(Font.font(24));
        title.setTextFill(Color.WHITE);
        Box.getChildren().add(title);

        GridPane f = styledGridPane();

        Label nameL = styledLabel("Username:");
        TextField nameField = styledTextField();

        Label emailL = styledLabel("Email:");
        TextField eField = styledTextField();

        Label pass = styledLabel("Password:");
        PasswordField pField = styledPasswordField();

        Label confirmPassLabel = styledLabel("Confirm Password:");
        PasswordField confirmPassF = styledPasswordField();

        // Error message label
        Label errorLabel = new Label();
        errorLabel.setFont(Font.font(12));
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        Button signUpButton = styledButton("Sign Up");
        signUpButton.setOnAction(e -> {
            String username = nameField.getText();
            String email = eField.getText();
            String password = pField.getText();
            String confirm = confirmPassF.getText();

            if (!password.equals(confirm)) {
                errorLabel.setText("Passwords do not match.");
                errorLabel.setVisible(true);
                return;
            }

            errorLabel.setVisible(false); // Hide the error label if no error
            String response = "";
            try {
                response = communicateWithServer("REGISTER", username, email, password);

                if ("0".equals(response)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful! You can now log in.");
                    stage.setScene(new Scene(createLoginPage(stage), 600, 400));
                } else {
                    if (response.equals("1")) {
                        errorLabel.setText("Username is not valid");
                        errorLabel.setVisible(true);
                    } else if (response.equals("2")) {
                        errorLabel.setText("Email is not valid");
                        errorLabel.setVisible(true);
                    } else if (response.equals("3")) {
                        errorLabel.setText("Password cannot be empty");
                        errorLabel.setVisible(true);
                    }
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Sign-up failed: " + ex.getMessage());
            }
        });

        Button loginButton = styledButton("Go to Login");
        loginButton.setOnAction(e -> {
            stage.setScene(new Scene(createLoginPage(stage), 600, 400));
        });

        f.add(nameL, 0, 0);
        f.add(nameField, 1, 0);
        f.add(emailL, 0, 1);
        f.add(eField, 1, 1);
        f.add(pass, 0, 2);
        f.add(pField, 1, 2);
        f.add(confirmPassLabel, 0, 3);
        f.add(confirmPassF, 1, 3);
        f.add(errorLabel, 1, 4); // Add error label below confirm password
        f.add(signUpButton, 1, 5);
        f.add(loginButton, 1, 6); // Adjust Login button's position

        Box.getChildren().add(f);
        layout.setCenter(Box);

        return layout;
    }
