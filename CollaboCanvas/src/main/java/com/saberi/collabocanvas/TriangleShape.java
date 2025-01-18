package com.saberi.collabocanvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.json.simple.JSONObject;

/**
 * Represents a triangle shape that can be drawn on a canvas.
 * Extends the {@link Shape} class and adds functionality for drawing a triangle with a specific base and height.
 */
public class TriangleShape extends Shape {
    private double base, height;



    /**
     * Constructs a TriangleShape with the specified position, base, height, stroke color, and fill color.
     *
     * @param x           The x-coordinate of the triangle's top vertex.
     * @param y           The y-coordinate of the triangle's top vertex.
     * @param base        The base length of the triangle.
     * @param height      The height of the triangle.
     * @param type        The type of shape (should be "triangle").
     * @param strokeColor The color of the triangle's outline.
     * @param fillColor   The color used to fill the triangle (not used in this class as fill is transparent).
     */
    public TriangleShape(double x, double y, double base, double height, String type, Color strokeColor, Color fillColor) {
        super(type, x, y, strokeColor, fillColor);
        this.base = base;
        this.height = height;
    }

