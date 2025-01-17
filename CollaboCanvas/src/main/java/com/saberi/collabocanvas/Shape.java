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

