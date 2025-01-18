package com.saberi.collabocanvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.json.simple.JSONObject;


/**
 * Represents a square shape with a specific size, position, and colors.
 * Extends the {@link Shape} class to provide square-specific functionality.
 */
public class SquareShape extends Shape {
    private double size; // Size of the square

    /**
     * Constructs a SquareShape with the specified properties.
     *
     * @param size        The size (width and height) of the square.
     * @param x           The x-coordinate of the square's top-left corner.
     * @param y           The y-coordinate of the square's top-left corner.
     * @param type        The type of the shape (e.g., "square").
     * @param strokeColor The stroke color of the square.
     * @param fillColor   The fill color of the square.
     */
    public SquareShape(double size, double x, double y, String type, Color strokeColor, Color fillColor) {
        super(type, x, y, strokeColor, fillColor);
        this.size = size;
    }
