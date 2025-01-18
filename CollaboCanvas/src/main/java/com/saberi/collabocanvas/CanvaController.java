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
    public void connectToServer(String host, int port) {
        try {
            // Establish the connection to the server
            socket = new Socket(host, port);

            // Set up the input and output streams
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Once connected, log a success message
            System.out.println("Connected to server at " + host + ":" + port);

            // Start listening for incoming data from the server
            startListening();

        } catch (IOException e) {
            System.out.println("Failed to connect to the server: " + e.getMessage());
        }
    }

    /**
     * Listens for messages from the server and processes them in a separate thread.
     * Handles actions such as drawing shapes, updating text, or other client-specific updates.
     */
    public void startListening() {
        List<Double> tempPointsX = new ArrayList<>();
        List<Double> tempPointsY = new ArrayList<>();
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    // Process the incoming message (expected in JSON format)
                    JSONParser parser = new JSONParser();
                    JSONObject jsonResponse = (JSONObject) parser.parse(message);
                    System.out.println("Received from server: " + jsonResponse.toString());

                    // Here you can parse the drawing data and update the canvas
                    String action = (String) jsonResponse.get("action"); // Action should be "draw" or other actions
                    if ("draw".equals(action)) {
                        // Parse drawing data and display it on the canvas
                        double x = ((Number) jsonResponse.get("x")).doubleValue();  // Casting to Number to retrieve double value
                        double y = ((Number) jsonResponse.get("y")).doubleValue();
                        String color = (String) jsonResponse.get("color");
                        double size = ((Number) jsonResponse.get("size")).doubleValue(); // Get the pen size
                        tempPointsX.clear();
                        tempPointsY.clear();
                        tempPointsX.add(x);
                        tempPointsY.add(y);

                        shapes.add(new FreehandShape(new ArrayList<>(tempPointsX), new ArrayList<>(tempPointsY), size));
                        // Call your method to update the drawing on the canvas with received data
                        Platform.runLater(() -> updateCanvas(jsonResponse)); // Ensure UI update happens on the JavaFX thread
                    } else if ("shape".equals(action)) {
                        // Handle shape drawing
                        Platform.runLater(() -> updateCanvas(jsonResponse));
                    } else if ("textdata".equals(action)) {
                        // Handle shape drawing
                        Platform.runLater(() -> updateCanvas(jsonResponse));
                    }
                }
            } catch (IOException e) {
                System.out.println("Error while listening for server messages: " + e.getMessage());
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
        }).start();
    }
    /**
     * Updates the canvas based on received JSON data.
     *
     * @param json The JSONObject containing drawing information (shapes, colors, sizes, etc.).
     */
    private void updateCanvas(JSONObject json) {
        if (json != null) {
            String action = (String) json.get("action");
            final double x = ((Number) json.get("x")).doubleValue();
            final double y = ((Number) json.get("y")).doubleValue();

            List<Double> tempPointsX = new ArrayList<>();
            List<Double> tempPointsY = new ArrayList<>();

            tempPointsX.clear();
            tempPointsY.clear();
            tempPointsX.add(x);
            tempPointsY.add(y);

            if ("shape".equals(action)) {
                double size = 1.0; // Default stroke width

                final String type = (String) json.get("type");

                final String color = ((String) json.get("strokeColor")).replace("0x", "#"); // Convert color
                final double strokeWidth = 1.0; // Default stroke size

                // Shape-specific dimensions
                final double radius = "circle".equals(type) && json.containsKey("radius")
                        ? ((Number) json.get("radius")).doubleValue()
                        : 0.0;
                final double width = ("rectangle".equals(type) || "square".equals(type)) && json.containsKey("width")
                        ? ((Number) json.get("width")).doubleValue()
                        : 0.0;
                final double height = "rectangle".equals(type) && json.containsKey("height")
                        ? ((Number) json.get("height")).doubleValue()
                        : 0.0;

                // Run drawing logic on JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    try {
                        Canvas canvas = (Canvas) getCanvasFromScene(); // Get the canvas
                        GraphicsContext gc = canvas.getGraphicsContext2D();

                        // Configure stroke properties
                        gc.setStroke(Color.web(color));
                        gc.setLineWidth(strokeWidth);

                        // Draw the requested shape
                        switch (type) {
                            case "circle":
                                redrawAllShapes();
                                gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
                                shapes.add(new CircleShape(x, y, radius, "circle", Color.WHITE, Color.WHITE));
                                break;

                            case "rectangle":
                                redrawAllShapes();
                                gc.strokeRect(x, y, width, height);
                                shapes.add(new RectangleShape(x, y, width, height, "rectangle", Color.WHITE, Color.WHITE));
                                break;

                            case "square":
                                redrawAllShapes();
                                // Calculate square size and ensure valid dimensions
                                double squareSize = Math.abs(width);

                                if (squareSize == 0) {
                                    System.out.println("Square width is zero, cannot draw.");
                                    break;
                                }
                                gc.strokeRect(x, y, squareSize, squareSize);
                                System.out.println("Square drawn at (" + x + ", " + y + ") with size: " + squareSize);
                                shapes.add(new SquareShape(size, x, y, "square", Color.WHITE, Color.WHITE));
                                break;
                            case "triangle":

                                redrawAllShapes();
                                // Stop drawing if the triangle becomes too small
                                if (currentBase < 10 || currentHeight < 10) {
                                    System.out.println("Triangle size is too small to draw further.");
                                    break;
                                }

                                // Calculate the vertices for the reversed triangle
                                double[] xPointsReversed = {x, x - currentBase / 2, x + currentBase / 2};
                                double[] yPointsReversed = {y + currentHeight, y, y};

                                // Draw the triangle
                                gc.setFill(Color.TRANSPARENT); // Transparent fill
                                gc.strokePolygon(xPointsReversed, yPointsReversed, 3);

                                // Shrink the triangle dimensions for the next draw
                                currentBase *= scaleFactor;
                                currentHeight *= scaleFactor;

                                // Log the triangle dimensions for debugging
                                System.out.println("Triangle drawn at (" + x + ", " + y + ") reversed with base: " + currentBase + " and height: " + currentHeight);
                                shapes.add(new TriangleShape(x, y, currentBase, currentHeight, "triangle", Color.WHITE, Color.WHITE));
                                break;
                            default:
                                System.out.println("Unsupported shape type: " + type);
                        }
                    } catch (Exception e) {
                        System.out.println("Error while drawing shape: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            } else if ("draw".equals(action)) {
                double size = (double) json.get("size"); // Default size for shapes (adjust if needed)
                String color = (String) json.get("color");
                // Handle freehand drawing or continuous drawing
                // Add new coordinates to the freehand list
                shapes.add(new FreehandShape(new ArrayList<>(tempPointsX), new ArrayList<>(tempPointsY), size));
                javafx.application.Platform.runLater(() -> {
                    // Get the GraphicsContext from the canvas
                    Canvas canvas = (Canvas) getCanvasFromScene();  // Get the canvas from the scene graph
                    GraphicsContext gc = canvas.getGraphicsContext2D();

                    // Set the stroke color
                    gc.setStroke(Color.web("#" + color.substring(2)));
                    gc.setLineWidth(size); // Use the pen size

                    // Start a new path (necessary if not already started)
                    gc.beginPath();

                    // Move to the first point (if needed) to start drawing
                    gc.moveTo(x, y);

                    // Draw the line to the new point
                    gc.lineTo(x, y);
                    gc.stroke();
                });
            } else if ("textdata".equals(action)) {
                String text = (String) json.get("text");

                double size = 20; // Size for text font


                // Ensure that this part runs on the JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    // Get the GraphicsContext from the canvas
                    Canvas canvas = (Canvas) getCanvasFromScene();  // Get the canvas from the scene graph
                    GraphicsContext gc = canvas.getGraphicsContext2D();

                    // Set the font size and color for the text
                    gc.setFont(new Font(size)); // Set the font size
                    gc.setFill(Color.BLACK); // Set text color

                    // Draw the text at the specified position (x, y)
                    gc.fillText(text, x, y);
                });
            }

        }
    }

    /**
     * Retrieves the primary {@code Canvas} object from the current scene hierarchy.
     *
     * @return the {@code Canvas} object if found; {@code null} otherwise.
     */
    // Helper class to store shape data
    private Canvas getCanvasFromScene() {
        // Get the root AnchorPane from the scene
        AnchorPane root = (AnchorPane) getPrimaryStage().getScene().getRoot();

        // Find the SplitPane from the root AnchorPane
        SplitPane splitPane = (SplitPane) root.getChildren().get(1);

        // Access the left AnchorPane in the SplitPane
        AnchorPane leftPane = (AnchorPane) splitPane.getItems().get(0);

        // Find the Pane containing the Canvas
        Pane canvasPane = (Pane) leftPane.getChildren().stream()
                .filter(node -> node instanceof Pane)
                .findFirst()
                .orElse(null);

        if (canvasPane != null) {
            // Find the Canvas inside the Pane
            return (Canvas) canvasPane.getChildren().stream()
                    .filter(node -> node instanceof Canvas)
                    .findFirst()
                    .orElse(null);
        }

        return null; // Return null if Canvas is not found
    }
    /**
     * Retrieves the primary {@code Stage} of the application.
     *
     * @return the primary {@code Stage}.
     */
    private Stage getPrimaryStage() {
        return (Stage) Stage.getWindows().filtered(window -> window instanceof Stage).get(0);
    }
    /**
     * Sends drawing data to the server in JSON format.
     *
     * @param x     the x-coordinate of the drawing action.
     * @param y     the y-coordinate of the drawing action.
     * @param color the color used for drawing.
     * @param size  the size of the drawing tool.
     */
    /**
     * Method to send drawing data in JSON format.
     */
    public void sendDrawingData(double x, double y, String color, double size) {
        // Create JSON object for the drawing data
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "draw");
        jsonObject.put("x", x);
        jsonObject.put("y", y);
        jsonObject.put("color", color);
        jsonObject.put("size", size);

        // Send the data to the server
        out.println(jsonObject.toJSONString());
    }
    /**
     * Sends shape data to the server in JSON format.
     *
     * @param shape the {@code Shape} object to send.
     */
    public void sendShapeData(Shape shape) {
        // Create a JSON object for the shape
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", "shape");
        jsonObject.put("type", shape.getType());
        jsonObject.put("x", shape.getX());
        jsonObject.put("y", shape.getY());

        // Handle RectangleShape
        if (shape instanceof RectangleShape) {
            RectangleShape rectangle = (RectangleShape) shape;
            jsonObject.put("width", rectangle.getWidth());
            jsonObject.put("height", rectangle.getHeight());
        }

        // Handle CircleShape
        if (shape instanceof CircleShape) {
            CircleShape circle = (CircleShape) shape;
            jsonObject.put("radius", circle.getRadius());
        }

        // Add stroke and fill color (if any)
        jsonObject.put("strokeColor", shape.getStrokeColor().toString());
        jsonObject.put("fillColor", shape.getFillColor() != null ? shape.getFillColor().toString() : "null");

        // Send the JSON object to the server
        out.println(jsonObject.toJSONString());
    }
    /**
     * Sends text data to the server in JSON format.
     *
     * @param x    the x-coordinate for placing the text.
     * @param y    the y-coordinate for placing the text.
     * @param text the text content.
     */
}


