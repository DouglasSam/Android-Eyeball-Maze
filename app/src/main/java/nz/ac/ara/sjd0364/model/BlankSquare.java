package nz.ac.ara.sjd0364.model;

import nz.ac.ara.sjd0364.model.enums.Color;
import nz.ac.ara.sjd0364.model.enums.Shape;
import nz.ac.ara.sjd0364.model.interfaces.Square;

/**
 * Blank unplayable square on the board.
 * @author sjd0364 - Samuel Douglas
 */
public class BlankSquare implements Square {

    @Override
    public Color getColor() {
        return Color.BLANK;
    }

    @Override
    public Shape getShape() {
        return Shape.BLANK;
    }
}
