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

    ates the styles of all tool buttons based on the current mode.
            */
    private void updateButtonStyles() {
        // Update pen and eraser button styles
        penButton.setStyle(
                "pen".equals(currentMode) ? "-fx-background-color: #0078d7; -fx-text-fill: white;" :
                        "-fx-background-color: #00c8ff; -fx-text-fill: white;"
        );
        eraserButton.setStyle(
                "eraser".equals(currentMode) ? "-fx-background-color: #0078d7; -fx-text-fill: white;" :
                        "-fx-background-color: #00c8ff; -fx-text-fill: white;"
        );

        // Update shape button styles
        rectangleButton.setStyle("rectangle".equals(currentMode) ? "-fx-background-color: #0078d7; -fx-text-fill: white;" : "");
        squareButton.setStyle("square".equals(currentMode) ? "-fx-background-color: #0078d7; -fx-text-fill: white;" : "");
        circleButton.setStyle("circle".equals(currentMode) ? "-fx-background-color: #0078d7; -fx-text-fill: white;" : "");
        triangleButton.setStyle("triangle".equals(currentMode) ? "-fx-background-color: #0078d7; -fx-text-fill: white;" : "");
    }
    /**
     * Adds text to the canvas.
     *
     * @param x    The X-coordinate of the text position.
     * @param y    The Y-coordinate of the text position.
     * @param text The text to add.
     */
    // Add a list to store the added texts
    // Add to your existing canvas drawing logic
    private void addTextToCanvas(double x, double y, String text) {
        System.out.println("Adding text to canvas: " + text + " at (" + x + ", " + y + ")");
        gc.setFont(Font.font("Arial", penSize * 5)); // Use dynamic font size
        gc.setFill(penColor); // Set color
        gc.fillText(text, x, y); // Draw text

        // Store text as a shape
        shapes.add(new TextShape(x, y, text, penColor)); // Add text as a shape
    }
    /**
     * Configures the canvas for drawing, including event handlers for mouse actions.
     *
     * @param gc The GraphicsContext for the canvas.
     */
    private void setupCanvasDrawing(GraphicsContext gc) {
        // Flag to track if a shape is being drawn
        AtomicBoolean isDrawingShape = new AtomicBoolean(false);

        // Temporary storage for freehand drawing points
        List<Double> tempPointsX = new ArrayList<>();
        List<Double> tempPointsY = new ArrayList<>();

        // Handle mouse press for shape drawing
        DrawingCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if ("pen".equals(currentMode)) {
                gc.beginPath(); // Start a new path
                gc.moveTo(event.getX(), event.getY());
                gc.stroke();
                tempPointsX.clear();
                tempPointsY.clear();
                tempPointsX.add(event.getX());
                tempPointsY.add(event.getY());
            } else if ("eraser".equals(currentMode)) {
                gc.beginPath();
                gc.moveTo(event.getX(), event.getY());
                gc.stroke();
            } else if ("rectangle".equals(currentMode) || "square".equals(currentMode) ||
                    "circle".equals(currentMode) || "triangle".equals(currentMode)) {
                startX = event.getX();
                startY = event.getY();
                isDrawingShape.set(true); // Start drawing shape
            }
        });

        // Handle mouse dragged to continue drawing
        DrawingCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if ("pen".equals(currentMode)) {
                tempPointsX.add(event.getX());
                tempPointsY.add(event.getY());
                gc.setLineWidth(penSize); // Use adjustable pen size
                gc.setStroke(penColor); // Pen uses the selected color
                gc.lineTo(event.getX(), event.getY());
                gc.stroke();
                gc.setLineWidth(penSize);

                // Send drawing data to the server
                sendDrawingData(event.getX(), event.getY(), penColor.toString(), penSize); // Send the current coordinates and color
            } else if ("eraser".equals(currentMode)) {
                gc.setLineWidth(penSize); // Use adjustable eraser size
                gc.setStroke(Color.WHITE); // Eraser uses white color
                gc.lineTo(event.getX(), event.getY());
                gc.stroke();

                // Remove shapes near the eraser's position
                eraseShape(event.getX(), event.getY());
            } else if ("rectangle".equals(currentMode) || "square".equals(currentMode) ||
                    "circle".equals(currentMode) || "triangle".equals(currentMode)) {
                // Redraw all previous shapes
                gc.clearRect(0, 0, DrawingCanvas.getWidth(), DrawingCanvas.getHeight());
                for (Shape shape : shapes) {
                    shape.draw(gc);
                }

                // Draw the current shape being dragged
                if ("rectangle".equals(currentMode)) {
                    double width = event.getX() - startX;
                    double height = event.getY() - startY;
                    gc.setFill(Color.TRANSPARENT); // Transparent fill
                    gc.strokeRect(startX, startY, width, height);
                    //sendShapeData(new RectangleShape(startX, startY, width, height, "rectangle", (Color) gc.getStroke(), (Color) gc.getFill()));
                } else if ("square".equals(currentMode)) {
                    double size = Math.min(Math.abs(event.getX() - startX), Math.abs(event.getY() - startY));
                    gc.setFill(Color.TRANSPARENT); // Transparent fill
                    gc.strokeRect(startX, startY, size, size);
                    //sendShapeData(new SquareShape(size, startX, startY, "square", (Color) gc.getStroke(), (Color) gc.getFill()));
                } else if ("circle".equals(currentMode)) {
                    double radius = Math.sqrt(Math.pow(event.getX() - startX, 2) + Math.pow(event.getY() - startY, 2));
                    gc.setFill(Color.TRANSPARENT); // Transparent fill
                    gc.strokeOval(startX - radius, startY - radius, 2 * radius, 2 * radius);
                    //sendShapeData(new CircleShape(startX, startY, radius, "circle", (Color) gc.getStroke(), (Color) gc.getFill()));
                } else if ("triangle".equals(currentMode)) {
                    double base = Math.abs(event.getX() - startX);
                    double height = Math.abs(event.getY() - startY);
                    gc.setFill(Color.TRANSPARENT); // Transparent fill
                    gc.strokePolygon(
                            new double[]{startX, startX + base / 2, startX - base / 2},
                            new double[]{startY, startY - height, startY - height},
                            3
                    );
                    //sendShapeData(new TriangleShape(startX, startY, base, height, "triangle", (Color) gc.getStroke(), (Color) gc.getFill()));
                }
            }
        });

        // Handle mouse release to finalize the shape
        DrawingCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if ("pen".equals(currentMode)) {
                shapes.add(new FreehandShape(new ArrayList<>(tempPointsX), new ArrayList<>(tempPointsY), penSize));
            } else if ("eraser".equals(currentMode)) {
                gc.closePath();
            } else if ("rectangle".equals(currentMode)) {
                double width = event.getX() - startX;
                double height = event.getY() - startY;

                shapes.add(new RectangleShape(startX, startY, width, height, "rectangle", (Color) gc.getStroke(), (Color) gc.getFill()));
                sendShapeData(new RectangleShape(startX, startY, width, height, "rectangle", (Color) gc.getStroke(), (Color) gc.getFill()));

            } else if ("square".equals(currentMode)) {
                double size = Math.min(Math.abs(event.getX() - startX), Math.abs(event.getY() - startY));
                shapes.add(new SquareShape(startX, startY, size, "square", (Color) gc.getStroke(), (Color) gc.getFill()));
                sendShapeData(new SquareShape(size, startX, startY, "square", (Color) gc.getStroke(), (Color) gc.getFill()));

            } else if ("circle".equals(currentMode)) {
                double radius = Math.sqrt(Math.pow(event.getX() - startX, 2) + Math.pow(event.getY() - startY, 2));
                shapes.add(new CircleShape(startX, startY, radius, "circle", (Color) gc.getStroke(), (Color) gc.getFill()));
                sendShapeData(new CircleShape(startX, startY, radius, "circle", (Color) gc.getStroke(), (Color) gc.getFill()));

            } else if ("triangle".equals(currentMode)) {
                double base = Math.abs(event.getX() - startX);
                double height = Math.abs(event.getY() - startY);
                shapes.add(new TriangleShape(startX, startY, base, height, "triangle", (Color) gc.getStroke(), (Color) gc.getFill()));
                sendShapeData(new TriangleShape(startX, startY, base, height, "triangle", (Color) gc.getStroke(), (Color) gc.getFill()));

            }
        });

        // Handle mouse click for text mode with dynamic text positioning
        DrawingCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("Mouse clicked at: " + event.getX() + ", " + event.getY()); // Debugging output
            if ("text".equals(currentMode) && !textToAdd.isEmpty()) {
                System.out.println("Adding text: " + textToAdd); // Debugging output
                addTextToCanvas(event.getX(), event.getY(), textToAdd);
                sendTextDataToServer(event.getX(), event.getY(), textToAdd);
            }
        });
    }
    /**
     * Connects to the server using the specified host and port.
     * Establishes input and output streams for communication.
     *
     * @param host The hostname or IP address of the server to connect to.
     * @param port The port number on which the server is listening.
     */
}

