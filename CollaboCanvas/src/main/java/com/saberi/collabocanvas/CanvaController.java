package com.saberi.collabocanvas;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jdk.internal.icu.text.UnicodeSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
/**
 * Controller class for the collaborative canvas application, responsible for handling user interactions
 * and managing the drawing functionalities on the canvas.
 * <p>
 * This class is part of the CollaboCanvas project, designed to facilitate collaborative drawing
 * and messaging in real time. It integrates drawing tools, shape creation, erasing, text addition,
 * message handling, and file operations like saving and loading.
 * </p>
 *
 * @author Mohammad Jafar Saberi
 * @author Zabihullah Mohammadi
 * @version 1.0
 * @since 2025-01-18
 */
public class CanvaController {
    /**
     * MenuItem for saving the canvas content to a file.
     */
    @FXML
    private MenuItem saveMenuItem;

    /**
     * MenuItem for opening a saved canvas file.
     */
    @FXML
    private MenuItem openMenuItem;

    /**
     * The main drawing canvas.
     */
    @FXML
    private Canvas DrawingCanvas;

    /**
     * Button to activate the pen mode for freehand drawing.
     */
    @FXML
    private Button penButton;

    /**
     * Button to activate the eraser mode for erasing parts of the canvas.
     */
    @FXML
    private Button eraserButton;

    /**
     * Button to activate rectangle drawing mode.
     */
    @FXML
    private Button rectangleButton;

    /**
     * Button to activate square drawing mode.
     */
    @FXML
    private Button squareButton;

    /**
     * Button to activate circle drawing mode.
     */
    @FXML
    private Button circleButton;

    /**
     * Button to activate triangle drawing mode.
     */
    @FXML
    private Button triangleButton;

    /**
     * ColorPicker to select the pen or shape color.
     */
    @FXML
    private ColorPicker colorPicker;

    /**
     * Slider to adjust the pen size.
     */
    @FXML
    private Slider penSizeSlider;

    /**
     * Button to clear the canvas.
     */
    @FXML
    private Button clearButton;

    /**
     * Button to activate the text mode for adding text to the canvas.
     */
    @FXML
    private Button textButton;

    /**
     * ListView to display the list of connected clients.
     */
    @FXML
    private static ListView<String> connectedClientList;

    /**
     * Button to send a chat message.
     */
    @FXML
    private Button sendButton;

    /**
     * TextField for user input of chat messages.
     */
    @FXML
    private TextField messageTextField;

    /**
     * TextArea to display chat messages.
     */
    @FXML
    private TextArea messageTextArea;

    /**
     * Socket connection for client-server communication.
     */
    private Socket socket;

    /**
     * Output stream to send data to the server.
     */
    private PrintWriter out;

    /**
     * Indicates if text mode is currently active.
     */
    private boolean isTextMode = false;

    /**
     * Tracks the current drawing mode (e.g., "pen", "eraser", or shape mode).
     */
    private String currentMode = "";

    /**
     * The current pen size.
     */
    private double penSize = 5.0;

    /**
     * The current pen color.
     */
    private Color penColor = Color.BLACK;

    /**
     * Starting X-coordinate for drawing shapes.
     */
    private double startX;

    /**
     * Starting Y-coordinate for drawing shapes.
     */
    private double startY;

    /**
     * GraphicsContext for drawing on the canvas.
     */
    private GraphicsContext gc;

    /**
     * Stores the text to be added to the canvas.
     */
    private String textToAdd = "";

    /**
     * List of shapes currently on the canvas, used for redrawing.
     */
    private List<Shape> shapes = new ArrayList<>();
    /**
     * Initializes the canvas, its tools, and event handlers.
     */

    private List<String> shapesType = new ArrayList<>();   // List to hold shape types (circle, rectangle, etc.)
    private List<Double> shapesX = new ArrayList<>();      // List to hold X coordinates of the shapes
    private List<Double> shapesY = new ArrayList<>();      // List to hold Y coordinates of the shapes
    private List<Double> shapesWidth = new ArrayList<>();  // List to hold width (used for rectangles and squares)
    private List<Double> shapesHeight = new ArrayList<>(); // List to hold height (used for rectangles)
    private List<Double> shapesRadius = new ArrayList<>(); // List to hold radius (used for circles)
    private List<String> shapesColor = new ArrayList<>();  // List to hold stroke color
    private List<Double> shapesSize = new ArrayList<>();   // List to hold stroke width
    private List<Double> freehandX = new ArrayList<>();    // List to store x-coordinates of the freehand drawing
    private List<Double> freehandY = new ArrayList<>();    // List to store y-coordinates of the freehand drawing

    private double currentBase = 100; // Initial base size
    private double currentHeight = 100; // Initial height size
    private final double scaleFactor = 0.9; // Constant factor to shrink the triangle

    private Socket socket1;
    private PrintWriter out1;
    private BufferedReader in1;

    private List<TextShape> textShapes = new ArrayList<>();
    private BufferedReader in;

    /**
     * Initializes the canvas, its tools, and event handlers.
     */
    @FXML
    public void initialize() {
        gc = DrawingCanvas.getGraphicsContext2D();
        setupCanvasDrawing(gc);

        showOnlineClients();

        // Configure the pen button to toggle the pen mode
        penButton.setOnAction(event -> togglePenMode());

        // Configure the eraser button to toggle the eraser mode
        eraserButton.setOnAction(event -> toggleEraserMode());

        // Configure the shape buttons
        rectangleButton.setOnAction(event -> toggleShapeMode("rectangle"));
        squareButton.setOnAction(event -> toggleShapeMode("square"));
        circleButton.setOnAction(event -> toggleShapeMode("circle"));
        triangleButton.setOnAction(event -> toggleShapeMode("triangle"));

        // Set up the ColorPicker to change the pen color
        colorPicker.setValue(penColor); // Set default color
        colorPicker.setOnAction(event -> penColor = colorPicker.getValue());

        // Set up the Slider to adjust pen size
        penSizeSlider.setValue(penSize); // Set default value
        penSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            penSize = newValue.doubleValue(); // Update pen size when Slider value changes
        });

        clearButton.setOnAction(event -> handleClearCanvas());
        textButton.setOnAction(event -> {
            currentMode = "text";
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Enter Text");
            dialog.setHeaderText("Please enter the text you want to add:");
            dialog.setContentText("Text:");

            dialog.showAndWait().ifPresent(inputText -> {
                System.out.println("Input Text: " + inputText); // Debugging output
                if (!inputText.isEmpty()) {
                    textToAdd = inputText;
                }
            });
        });
        DrawingCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if ("eraser".equals(currentMode)) {
                gc.setLineWidth(penSize);
                gc.setStroke(Color.WHITE);
                gc.lineTo(event.getX(), event.getY());
                gc.stroke();

                // Remove shapes and redraw canvas
                eraseShape(event.getX(), event.getY());
                gc.clearRect(0, 0, DrawingCanvas.getWidth(), DrawingCanvas.getHeight());
                for (Shape shape : shapes) {
                    shape.draw(gc);
                }
            }
        });
        saveMenuItem.setOnAction(event -> handleSave());
        openMenuItem.setOnAction(event -> handleOpenFromFolder());

        sendButton.setOnAction(event -> {
            String message = messageTextField.getText();  // Get the text from the TextField
            if (message != null && !message.trim().isEmpty()) {
                sendMessageToServer(message);  // Send the message to the server
                appendMessageToTextArea("You: " + message);  // Append the message to the TextArea
                messageTextField.clear();  // Clear the TextField after sending the message
            }
        });
    }
    /**
     * Sends a message to the server.
     *
     * @param message The message to be sent.
     */
    private void sendMessageToServer(String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", message);
        if (out1 != null) {
            out1.println(jsonObject.toJSONString());  // Send the message to the server
        }
    }
    /**
     * Appends a message to the chat TextArea.
     *
     * @param message The message to append.
     */
    // Method to append the message to the TextArea
    private void appendMessageToTextArea(String message) {
        messageTextArea.appendText(message + "\n");  // Add the message to the TextArea with a newline
        messageTextArea.setScrollTop(Double.MAX_VALUE);  // Scroll to the bottom of the TextArea
    }
    /**
     * Handles the erasing of shapes on the canvas.
     *
     * @param x The X-coordinate of the eraser position.
     * @param y The Y-coordinate of the eraser position.
     */
    private void eraseShape(double x, double y) {
        shapes.removeIf(shape -> shape.intersects(x, y, penSize, penSize)); // Remove shapes in eraser area
    }

    gles the pen drawing mode.
     */
    private void togglePenMode() {
        if ("pen".equals(currentMode)) {
            deactivateAllModes(); // Deactivate if already in pen mode
        } else {
            currentMode = "pen";
            updateButtonStyles();
        }
    }
    /**
     * Toggles the eraser mode.
     */
    private void toggleEraserMode() {
        if ("eraser".equals(currentMode)) {
            deactivateAllModes(); // Deactivate if already in eraser mode
        } else {
            currentMode = "eraser";
            updateButtonStyles();
        }
    }
    /**
     * Clears the canvas and resets drawing settings.
     */
    @FXML
    private void handleClearCanvas() {
        // Clear the canvas
        gc.clearRect(0, 0, DrawingCanvas.getWidth(), DrawingCanvas.getHeight());

        // Clear the shapes list
        shapes.clear();

        // Optionally, reset other settings (e.g., pen size, color, mode)
        currentMode = "";
        updateButtonStyles();
    }
    /**
     * Toggles the mode for drawing shapes.
     *
     * @param shape The shape to toggle (e.g., "rectangle", "circle").
     */
    private void toggleShapeMode(String shape) {
        if (currentMode.equals(shape)) {
            // Deactivate shape mode if clicked again
            deactivateAllModes();
        } else {
            // Activate the selected shape mode
            currentMode = shape;
            updateButtonStyles();
        }
    }

    private void deactivateAllModes() {
        currentMode = ""; // Reset the mode to default (no active mode)
        updateButtonStyles();
    }

}

