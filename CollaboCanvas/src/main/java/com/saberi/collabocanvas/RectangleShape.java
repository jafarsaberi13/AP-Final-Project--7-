package com.saberi.collabocanvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.json.simple.JSONObject;


/**
 * Represents a rectangular shape that can be drawn on a canvas.
 * Extends the abstract {@link Shape} class and provides specific implementation for rectangles.
 */
public class RectangleShape extends Shape {
    private double width, height;


    /**
     * Constructs a new {@code RectangleShape} instance.
     *
     * @param x           The x-coordinate of the rectangle's top-left corner.
     * @param y           The y-coordinate of the rectangle's top-left corner.
     * @param width       The width of the rectangle.
     * @param height      The height of the rectangle.
     * @param type        The type of the shape (e.g., "rectangle").
     * @param strokeColor The color used for the rectangle's border.
     * @param fillColor   The color used to fill the rectangle.
     */
    public RectangleShape(double x, double y, double width, double height, String type, Color strokeColor, Color fillColor) {
        super(type, x, y, strokeColor, fillColor);
        this.width = width;
        this.height = height;
    }
