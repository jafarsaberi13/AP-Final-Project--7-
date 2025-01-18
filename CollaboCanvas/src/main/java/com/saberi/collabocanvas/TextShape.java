package com.saberi.collabocanvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


/**
 * Represents a text shape that can be drawn on a canvas.
 * Extends the {@link Shape} class and adds functionality for displaying text at a specific position.
 */
public class TextShape extends Shape {
    private String text;


    /**
     * Constructs a TextShape with the specified position, text, and color.
     *
     * @param x     The x-coordinate where the text will be displayed.
     * @param y     The y-coordinate where the text will be displayed.
     * @param text  The text content to display.
     * @param color The color of the text.
     */
    public TextShape(double x, double y, String text, Color color) {
        //super(x, y, color);
        super();
        this.text = text;
    }


    /**
     * Draws the text on the specified {@link GraphicsContext}.
     *
     * @param gc The {@link GraphicsContext} used to draw the text.
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.setFont(Font.font("Arial", 20)); // You can modify font size and family
        gc.fillText(text, x, y);
    }


    /**
     * Determines if the text intersects with a specified rectangular area.
     * Currently, this method always returns {@code false} because text does not have a bounding box.
     *
     * @param x      The x-coordinate of the rectangle's top-left corner.
     * @param y      The y-coordinate of the rectangle's top-left corner.
     * @param width  The width of the rectangle.
     * @param height The height of the rectangle.
     * @return {@code false} (no intersection logic implemented for text shapes).
     */
    @Override
    public boolean intersects(double x, double y, double width, double height) {
        return false;
    }


    /**
     * Gets the text content of the shape.
     *
     * @return The text content.
     */
    // Getter for the text
    public String getText() {
        return text;
    }


    /**
     * Sets the text content of the shape.
     *
     * @param text The new text content to display.
     */
    // Setter for the text, if you need to modify it later
    public void setText(String text) {
        this.text = text;
    }
}
