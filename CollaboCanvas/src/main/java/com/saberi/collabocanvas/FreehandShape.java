package com.saberi.collabocanvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.List;


/**
 * The {@code FreehandShape} class represents a freehand drawing shape on the canvas.
 * It consists of a sequence of points, defining the freehand path.
 */
public class FreehandShape extends Shape {
    private List<Double> pointsX;
    private List<Double> pointsY;
    private double penSize;

    /**
     * Constructs a new {@code FreehandShape} with the specified points and pen size.
     *
     * @param pointsX the list of X coordinates for the freehand shape.
     * @param pointsY the list of Y coordinates for the freehand shape.
     * @param penSize the size of the pen used to draw the shape.
     */
    public FreehandShape(List<Double> pointsX, List<Double> pointsY, double penSize) {
        super(); // Call the parent class constructor
        this.pointsX = pointsX;
        this.pointsY = pointsY;
        this.penSize = penSize;
    }