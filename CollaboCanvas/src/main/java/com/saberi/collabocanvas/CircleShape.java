package com.saberi.collabocanvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.json.simple.JSONObject;
/**
 * Represents a circular shape in the CollaboCanvas application.
 * Extends the {@link Shape} class to include radius-specific properties and behaviors.
 */
public class CircleShape extends Shape {
    /** The radius of the circle. */
    private double radius;

    /**
     * Constructs a new {@code CircleShape} object.
     *
     * @param x           the x-coordinate of the circle's center.
     * @param y           the y-coordinate of the circle's center.
     * @param radius      the radius of the circle.
     * @param type        the type of the shape (e.g., "circle").
     * @param strokeColor the stroke color of the circle.
     * @param fillColor   the fill color of the circle (can be {@code null} for no fill).
     */
    public CircleShape(double x, double y, double radius, String type, Color strokeColor, Color fillColor) {
        super(type, x, y, strokeColor, fillColor);
        this.radius = radius;
    }
