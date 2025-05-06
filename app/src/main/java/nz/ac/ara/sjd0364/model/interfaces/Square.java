package nz.ac.ara.sjd0364.model.interfaces;


import nz.ac.ara.sjd0364.model.enums.Color;
import nz.ac.ara.sjd0364.model.enums.Shape;

/**
 * A square on the game board.
 * @author sjd0364 - Samuel Douglas
 */
public interface Square {

    public Color getColor();
    public Shape getShape();

}
