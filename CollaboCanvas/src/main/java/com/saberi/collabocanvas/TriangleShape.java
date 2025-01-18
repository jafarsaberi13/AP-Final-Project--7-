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


    /**
     * Draws the triangle on the specified {@link GraphicsContext}.
     * The triangle is outlined with the specified stroke color and has a transparent fill.
     *
     * @param gc The {@link GraphicsContext} used to draw the triangle.
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.TRANSPARENT); // Transparent fill
        gc.setStroke(color); // Set the stroke color
        gc.setLineWidth(2); // Set stroke width explicitly
        gc.strokePolygon( // Use strokePolygon to only outline the triangle
                new double[]{x, x + base / 2, x - base / 2},
                new double[]{y, y - height, y - height},
                3
        );
    }


    /**
     * Checks if the triangle intersects with a specified rectangular area.
     * The intersection is checked using the bounding box of the triangle.
     *
     * @param x      The x-coordinate of the rectangle's top-left corner.
     * @param y      The y-coordinate of the rectangle's top-left corner.
     * @param width  The width of the rectangle.
     * @param height The height of the rectangle.
     * @return {@code true} if the triangle intersects with the specified rectangle, {@code false} otherwise.
     */
    @Override
    public boolean intersects(double x, double y, double width, double height) {
        // Bounding box for the triangle to check if the eraser intersects the shape
        return this.x - base / 2 < x + width && this.x + base / 2 > x &&
                this.y - this.height < y + height && this.y > y;
    }


    /**
     * Converts the triangle shape into a JSON object representation.
     * The JSON object includes the type, position (x, y), base, and height of the triangle.
     *
     * @return A {@link JSONObject} representing the triangle shape.
     */
    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("type", "TriangleShape");
        json.put("base", this.base);
        json.put("height", this.height);
        return json;
    }


    /**
     * Gets the base length of the triangle.
     *
     * @return The base length of the triangle.
     */
    // Getters and setters for base and height
    public double getBase() {
        return base;
    }

    /**
     * Sets the base length of the triangle.
     *
     * @param base The new base length of the triangle.
     */
    public void setBase(double base) {
        this.base = base;
    }
    /**
     * Gets the height of the triangle.
     *
     * @return The height of the triangle.
     */
    public double getHeight() {
        return height;
    }
    /**
     * Sets the height of the triangle.
     *
     * @param height The new height of the triangle.
     */
    public void setHeight(double height) {
        this.height = height;
    }
}
