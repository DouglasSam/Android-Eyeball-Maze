package nz.ac.ara.sjd0364.model;

import nz.ac.ara.sjd0364.model.enums.Color;
import nz.ac.ara.sjd0364.model.enums.Shape;
import nz.ac.ara.sjd0364.model.interfaces.Square;

/**
 * A playable square on the game board that will have a color and shape.
 * @author sjd0364 - Samuel Douglas
 */
public class PlayableSquare implements Square {

    private final Color color;
    private final Shape shape;

    public PlayableSquare(Color color, Shape shape) {
        super();
        this.color = color;
        this.shape = shape;
    }

    public Color getColor() {
        return color;
    }

    public Shape getShape() {
        return shape;
    }

}
