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

    /**
     * Draws the circle on the canvas using the provided {@code GraphicsContext}.
     *
     * @param gc the {@code GraphicsContext} used to draw the circle.
     */

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.TRANSPARENT); // Transparent fill
        gc.setStroke(color); // Set the stroke color
        gc.setLineWidth(2); // Set stroke width explicitly
        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2); // Outline the circle
    }

    /**
     * Checks if the circle intersects with a rectangular area, such as an eraser.
     *
     * @param x      the x-coordinate of the rectangle.
     * @param y      the y-coordinate of the rectangle.
     * @param width  the width of the rectangle.
     * @param height the height of the rectangle.
     * @return {@code true} if the circle intersects with the rectangle; {@code false} otherwise.
     */
    @Override
    public boolean intersects(double x, double y, double width, double height) {
        // Checking for intersection with the eraser
        double closestX = Math.max(x, Math.min(this.x, x + width));
        double closestY = Math.max(y, Math.min(this.y, y + height));
        double distanceSquared = Math.pow(this.x - closestX, 2) + Math.pow(this.y - closestY, 2);
        return distanceSquared < Math.pow(radius, 2);
    }

    /**
     * Converts the circle's properties to a {@link JSONObject}.
     *
     * @return a {@code JSONObject} representing the circle.
     */
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("radius", this.radius);
        return json;
    }

    /**
     * Gets the radius of the circle.
     *
     * @return the radius of the circle.
     */
    public double getRadius() {
        return radius;
    }
}
