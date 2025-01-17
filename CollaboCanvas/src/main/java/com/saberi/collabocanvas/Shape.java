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