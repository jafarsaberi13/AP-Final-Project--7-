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

    /**
     * Creates the login page layout with input fields and buttons.
     *
     * @param stage the primary stage of the application.
     * @return a {@link BorderPane} containing the login page layout.
     */
    private BorderPane createLoginPage(Stage stage) {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #87CEEB;");

        VBox contentBox = styledVBox();

        Label title = new Label("Login");
        title.setFont(Font.font(24));
        title.setTextFill(Color.WHITE);
        contentBox.getChildren().add(title);

        GridPane f1 = styledGridPane();

        Label nameL = styledLabel("Username:");
        TextField nameField = styledTextField();

        Label passL = styledLabel("Password:");
        PasswordField passField = styledPasswordField();

        Label errorLabel = new Label();
        errorLabel.setFont(Font.font(12));
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        errorLabel.setVisible(false); // Hide the error label if no error

        Button Button = styledButton("Login");
        Button.setOnAction(e -> {
            String username = nameField.getText();
            String password = passField.getText();

            try {
                String response = communicateWithServer("LOGIN", username, "", password);

                if ("0".equals(response)) {

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/saberi/collabocanvas/Canva.fxml"));
                        AnchorPane root = loader.load();

                        // Get the controller reference
                        CanvaController controller = loader.getController();

                        // Connect to the server when the application starts
                        String serverHost = "localhost";  // Use the server's address
                        int serverPort = 7777;  // Use the server's port
                        controller.connectToServer(serverHost, serverPort);
                        controller.connectToServerMesseging(serverHost, 1111);

                        // Start listening for server messages
                        controller.startListening();

                        // Set the scene and show the stage
                        Scene scene = new Scene(root, 800, 600);
                        stage.setScene(scene);
                        stage.setTitle("Drawing and Chat Application");

                        stage.show();
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to load application: " + ex.getMessage());
                    }
                } else {
                    errorLabel.setText("Username/Password is not Valid");
                    errorLabel.setVisible(true);
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Login failed: " + ex.getMessage());
            }
        });

        f1.add(nameL, 0, 0);
        f1.add(nameField, 1, 0);
        f1.add(passL, 0, 1);
        f1.add(passField, 1, 1);
        f1.add(errorLabel, 1, 2);
        f1.add(Button, 1, 3);

        contentBox.getChildren().add(f1);
        layout.setCenter(contentBox);

        return layout;
    }

    /**
     * Creates a styled {@link VBox} layout with default spacing and alignment.
     *
     * @return a {@link VBox} with spacing of 20, padding of 15, and center alignment.
     */
    private VBox styledVBox() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    /**
     * Creates a styled {@link GridPane} layout with default spacing and alignment.
     *
     * @return a {@link GridPane} with center alignment, 15-pixel padding, and
     * horizontal/vertical gaps of 10 pixels.
     */
    private GridPane styledGridPane() {
        GridPane g = new GridPane();
        g.setAlignment(Pos.CENTER);
        g.setPadding(new Insets(15));
        g.setHgap(10);
        g.setVgap(10);
        return g;
    }

    /**
     * Creates a styled {@link Label} with a custom font size and color.
     *
     * @param text the text to display on the label.
     * @return a {@link Label} with 14px font size and black text color.
     */
    private Label styledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        return label;
    }

    /**
     * Creates a styled {@link TextField} with custom border and background color.
     *
     * @return a {@link TextField} styled with black borders, rounded corners,
     * and a light gray background.
     */
    private TextField styledTextField() {
        TextField textField = new TextField();
        textField.setStyle("-fx-border-color: black; -fx-border-radius: 5; -fx-background-color: #f0f0f0;");
        return textField;
    }

    /**
     * Creates a styled {@link PasswordField} with custom border and background color.
     *
     * @return a {@link PasswordField} styled with black borders, rounded corners,
     * and a light gray background.
     */
    private PasswordField styledPasswordField() {
        PasswordField pass = new PasswordField();
        pass.setStyle("-fx-border-color: black; -fx-border-radius: 5; -fx-background-color: #f0f0f0;");
        return pass;
    }

    /**
     * Creates a styled {@link Button} with custom colors and hover effects.
     *
     * @param text the text to display on the button.
     * @return a {@link Button} styled with a blue background, white text,
     * and hover effects to change background and text color.
     */
    private Button styledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #4682B4; -fx-text-fill: white;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #5A9BD4; -fx-text-fill: black;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #4682B4; -fx-text-fill: white;"));
        return button;
    }

    /**
     * Displays an alert dialog with the specified parameters.
     *
     * @param type    the type of the alert (e.g., INFORMATION, ERROR).
     * @param title   the title of the alert dialog.
     * @param message the message to display in the alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Establishes communication with the server for user registration or login.
     *
     * @param action   the action to perform, either "REGISTER" or "LOGIN".
     * @param username the user's username.
     * @param email    the user's email (only for registration).
     * @param password the user's password.
     * @return the server's response as a {@link String}.
     * @throws IOException if there is an error during communication.
     */
    private String communicateWithServer(String action, String username, String email, String password) throws IOException {
        JSONObject obj = new JSONObject();
        if (action.equals("REGISTER")) {
            obj.put("Type", action);
            obj.put("Username", username);
            obj.put("Email", email);
            obj.put("Password", password);
        } else { // If action be 'Login'
            obj.put("Type", action);
            obj.put("Username", username);
            obj.put("Password", password);
        }
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            DataInputStream in = new DataInputStream(socket.getInputStream());

            out.println(obj.toJSONString());
            System.out.println("sened " + obj.toJSONString());
            return in.readUTF();
        } catch (IOException io) {
            io.printStackTrace();
            return "Error";
        }
    }

    public static void main(String[] args) {
        Thread messagingThread = new Thread(() -> MessagingServer.startchatServer());
        Thread drawingThread = new Thread(() -> drawingServer.startDrawingServer());
        Thread connectionThread = new Thread(() -> Server.serverConnect());
        Thread uiThread = new Thread(() -> launch(args));

        // Start all threads
        messagingThread.start();
        drawingThread.start();
        connectionThread.start();
        uiThread.start();
        // Optionally, wait for threads to finish if necessary
        try {
            messagingThread.join();
            drawingThread.join();
            connectionThread.join();
            uiThread.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the JavaFX application by setting up the primary stage and scenes.
     *
     * @param primaryStage the primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("User Authentication");

        Scene signUpScene = new Scene(createSignUpPage(primaryStage), 600, 400);
        Scene logScene = new Scene(createLoginPage(primaryStage), 600, 400);

        primaryStage.setScene(signUpScene);
        primaryStage.show();
    }
}