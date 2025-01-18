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


    /**
     * Draws the rectangle on a specified {@link GraphicsContext}.
     * The rectangle is drawn with a transparent fill and a stroke.
     *
     * @param gc The {@code GraphicsContext} to draw the rectangle on.
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.TRANSPARENT); // Transparent fill
        gc.setStroke(color); // Set the stroke color
        gc.setLineWidth(2); // Set stroke width explicitly
        gc.strokeRect(x, y, width, height); // Outline the rectangle
    }


    /**
     * Checks if this rectangle intersects with another rectangle specified by its position and dimensions.
     *
     * @param x      The x-coordinate of the other rectangle's top-left corner.
     * @param y      The y-coordinate of the other rectangle's top-left corner.
     * @param width  The width of the other rectangle.
     * @param height The height of the other rectangle.
     * @return {@code true} if the rectangles intersect; {@code false} otherwise.
     */
    @Override
    public boolean intersects(double x, double y, double width, double height) {
        return this.x < x + width && this.x + this.width > x &&
                this.y < y + height && this.y + this.height > y;
    }


    /**
     * Converts this rectangle's data into a JSON object.
     *
     * @return A {@code JSONObject} representing the rectangle's attributes.
     */
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("type", "RectangleShape");
        json.put("width", this.width);
        json.put("height", this.height);
        return json;
    }

    /**
     * Retrieves the width of the rectangle.
     *
     * @return The width of the rectangle.
     */
    public double getWidth() {
        return width;
    }


    /**
     * Retrieves the height of the rectangle.
     *
     * @return The height of the rectangle.
     */
    public double getHeight() {
        return height;
    }
}
