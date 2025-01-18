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

    /**
     * Draws the square on the specified {@link GraphicsContext}.
     *
     * @param gc The {@link GraphicsContext} used to draw the square.
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.TRANSPARENT); // Transparent fill
        gc.setStroke(color); // Set the stroke color
        gc.setLineWidth(2); // Set stroke width explicitly
        gc.strokeRect(x, y, size, size);
    }


    /**
     * Determines if the square intersects with a specified rectangle.
     *
     * @param x      The x-coordinate of the rectangle's top-left corner.
     * @param y      The y-coordinate of the rectangle's top-left corner.
     * @param width  The width of the rectangle.
     * @param height The height of the rectangle.
     * @return {@code true} if the square intersects with the rectangle; {@code false} otherwise.
     */
    @Override
    public boolean intersects(double x, double y, double width, double height) {
        return this.x < x + width && this.x + size > x &&
                this.y < y + height && this.y + size > y;
    }


    /**
     * Converts the square's properties to a JSON representation.
     *
     * @return A {@link JSONObject} containing the square's properties.
     */
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("type", "SquareShape");
        json.put("size", this.size);
        return json;
    }


    /**
     * Gets the size of the square.
     *
     * @return The size of the square.
     */
    // Getter for size
    public double getSize() {
        return size;
    }

    /**
     * Sets the size of the square.
     *
     * @param size The new size of the square.
     */
    // Setter for size
    public void setSize(double size) {
        this.size = size;
    }
}
