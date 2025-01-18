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
