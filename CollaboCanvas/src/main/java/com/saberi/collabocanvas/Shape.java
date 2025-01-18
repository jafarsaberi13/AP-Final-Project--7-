package com.saberi.collabocanvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * Abstract base class representing a geometric shape.
 * Provides common properties and methods for shapes such as position, color,
 * and JSON serialization/deserialization.
 */
public abstract class Shape {
    private String type;
    protected double x, y; // Position of the shape
    protected Color color; // Color of the shape
    private Color strokeColor;
    private Color fillColor;


    /**
     * Constructor to create a shape with specific properties.
     *
     * @param type        The type of the shape (e.g., "rectangle", "circle").
     * @param x           The x-coordinate of the shape's position.
     * @param y           The y-coordinate of the shape's position.
     * @param strokeColor The stroke color of the shape.
     * @param fillColor   The fill color of the shape.
     */
    public Shape(String type, double x, double y, Color strokeColor, Color fillColor) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
    }


    // Getter for type
    public String getType() {
        return type;
    }

    /**
     * Default constructor to create a shape with default properties.
     * Sets position to (0, 0) and color to black.
     */
    public Shape() {
        this.x = 0;
        this.y = 0;
        this.color = Color.BLACK; // Default color
    }

    /**
     * Abstract method for drawing the shape. Must be implemented by subclasses.
     *
     * @param gc The {@link GraphicsContext} to draw the shape on.
     */
    // Abstract method for drawing the shape
    public abstract void draw(GraphicsContext gc);


    /**
     * Abstract method for checking if the shape intersects with a given rectangle.
     * Must be implemented by subclasses.
     *
     * @param x      The x-coordinate of the rectangle.
     * @param y      The y-coordinate of the rectangle.
     * @param width  The width of the rectangle.
     * @param height The height of the rectangle.
     * @return {@code true} if the shape intersects with the rectangle, otherwise {@code false}.
     */
    // Abstract method for checking intersection
    public abstract boolean intersects(double x, double y, double width, double height);


    /**
     * Converts the shape to a JSON representation.
     *
     * @return A {@link JSONObject} containing the shape's properties.
     */
    // Convert the shape to a JSON object
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("type", this.getClass().getSimpleName());
        json.put("x", this.x);
        json.put("y", this.y);
        //json.put("color", colorToHex(this.color));
        return json;
    }

    // Convert Color to Hex
//    private String colorToHex(Color color) {
//        return String.format("#%02X%02X%02X",
//                (int) (color.getRed() * 255),
//                (int) (color.getGreen() * 255),
//                (int) (color.getBlue() * 255));
//    }
    /**
     * Gets the x-coordinate of the shape.
     *
     * @return The x-coordinate of the shape.
     */
    public double getX() {
        return x;
    }
    /**
     * Gets the y-coordinate of the shape.
     *
     * @return The y-coordinate of the shape.
     */
    public double getY() {
        return y;
    }
    /**
     * Gets the color of the shape.
     *
     * @return The color of the shape.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the stroke color of the shape.
     *
     * @return The stroke color of the shape.
     */
    // Getter for stroke color
    public Color getStrokeColor() {
        return strokeColor;
    }


    /**
     * Gets the fill color of the shape.
     *
     * @return The fill color of the shape.
     */
    // Getter for fill color
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * Converts a JSON object to a specific shape instance.
     *
     * @param json The {@link JSONObject} containing the shape data.
     * @return A new {@link Shape} instance based on the JSON data.
     */

    public static Shape fromJson(JSONObject json) {
        String type = (String) json.get("type");
        double x = ((Number) json.get("x")).doubleValue();
        double y = ((Number) json.get("y")).doubleValue();
        Color strokeColor = json.containsKey("strokeColor")
                ? Color.web((String) json.get("strokeColor"))
                : Color.BLACK;
        Color fillColor = json.containsKey("fillColor")
                ? Color.web((String) json.get("fillColor"))
                : Color.TRANSPARENT;
        double height = 0;
        switch (type) {
            case "RectangleShape":
                double width = ((Number) json.get("width")).doubleValue();
                height = ((Number) json.get("height")).doubleValue();
                return new RectangleShape(x, y, width, height, "rectangle", strokeColor, fillColor);

            case "CircleShape":
                double radius = ((Number) json.get("radius")).doubleValue();
                return new CircleShape(x, y, radius, "circle", strokeColor, fillColor);

            case "TriangleShape":
                double base = ((Number) json.get("base")).doubleValue();
                double heightT = ((Number) json.get("height")).doubleValue();
                return new TriangleShape(x, y, base, height, "triangle", strokeColor, fillColor);

            case "FreehandShape":
                // Handle freehand shape (requires additional data, e.g., points)
                JSONArray pointsX = (JSONArray) json.get("pointsX");
                JSONArray pointsY = (JSONArray) json.get("pointsY");
                //double size = ((Number) json.get("size")).doubleValue();
                return new FreehandShape(pointsX, pointsY, 10);

            default:
                throw new IllegalArgumentException("Unknown shape type: " + type);
        }
    }

}
