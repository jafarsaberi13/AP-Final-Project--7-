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
    /**
     * Draws the freehand shape on the given {@code GraphicsContext}.
     *
     * @param gc the {@code GraphicsContext} to draw on.
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setLineWidth(penSize);
        gc.setStroke(color);

        gc.beginPath();
        if (pointsX != null) {
            for (int i = 0; i < pointsX.size() - 1; i++) {
                gc.moveTo(pointsX.get(i), pointsY.get(i));
                gc.lineTo(pointsX.get(i + 1), pointsY.get(i + 1));
            }
        }
        gc.stroke();
        gc.closePath();
    }


    /**
     * Checks if the freehand shape intersects a given rectangular area.
     *
     * @param x      the X coordinate of the rectangle's top-left corner.
     * @param y      the Y coordinate of the rectangle's top-left corner.
     * @param width  the width of the rectangle.
     * @param height the height of the rectangle.
     * @return {@code true} if the shape intersects the rectangle; {@code false} otherwise.
     */
    @Override
    public boolean intersects(double x, double y, double width, double height) {
        for (int i = 0; i < pointsX.size() - 1; i++) {
            double startX = pointsX.get(i);
            double startY = pointsY.get(i);
            double endX = pointsX.get(i + 1);
            double endY = pointsY.get(i + 1);

            // Check if any segment of the freehand line intersects the given rectangle
            if (lineIntersectsRect(startX, startY, endX, endY, x, y, width, height)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Checks if a line segment intersects with a given rectangle.
     *
     * @param x1 the X coordinate of the line's start point.
     * @param y1 the Y coordinate of the line's start point.
     * @param x2 the X coordinate of the line's end point.
     * @param y2 the Y coordinate of the line's end point.
     * @param rx the X coordinate of the rectangle's top-left corner.
     * @param ry the Y coordinate of the rectangle's top-left corner.
     * @param rw the width of the rectangle.
     * @param rh the height of the rectangle.
     * @return {@code true} if the line intersects the rectangle; {@code false} otherwise.
     */
    private boolean lineIntersectsRect(double x1, double y1, double x2, double y2, double rx, double ry, double rw, double rh) {
        // Check if the line intersects any of the rectangle's edges
        return lineIntersectsLine(x1, y1, x2, y2, rx, ry, rx + rw, ry) || // Top edge
                lineIntersectsLine(x1, y1, x2, y2, rx, ry, rx, ry + rh) || // Left edge
                lineIntersectsLine(x1, y1, x2, y2, rx + rw, ry, rx + rw, ry + rh) || // Right edge
                lineIntersectsLine(x1, y1, x2, y2, rx, ry + rh, rx + rw, ry + rh);   // Bottom edge
    }


    /**
     * Checks if two line segments intersect.
     *
     * @param x1 the X coordinate of the first line's start point.
     * @param y1 the Y coordinate of the first line's start point.
     * @param x2 the X coordinate of the first line's end point.
     * @param y2 the Y coordinate of the first line's end point.
     * @param x3 the X coordinate of the second line's start point.
     * @param y3 the Y coordinate of the second line's start point.
     * @param x4 the X coordinate of the second line's end point.
     * @param y4 the Y coordinate of the second line's end point.
     * @return {@code true} if the lines intersect; {@code false} otherwise.
     */
    private boolean lineIntersectsLine(double x1, double y1, double x2, double y2,
                                       double x3, double y3, double x4, double y4) {
        // Calculate the direction of the lines
        double det = (x2 - x1) * (y4 - y3) - (y2 - y1) * (x4 - x3);
        if (det == 0) {
            return false; // Lines are parallel
        }
        double lambda = ((y4 - y3) * (x4 - x1) + (x3 - x4) * (y4 - y1)) / det;
        double gamma = ((y1 - y2) * (x4 - x1) + (x2 - x1) * (y4 - y1)) / det;
        return (0 <= lambda && lambda <= 1) && (0 <= gamma && gamma <= 1);
    }
}
