package nz.ac.ara.sjd0364.model;

import nz.ac.ara.sjd0364.model.enums.Color;
import nz.ac.ara.sjd0364.model.enums.Direction;
import nz.ac.ara.sjd0364.model.enums.Shape;
import nz.ac.ara.sjd0364.model.interfaces.Square;

/**
 * Eyeball that represents the current position of the player.
 * @author sjd0364 - Samuel Douglas
 */
public class Eyeball {

    private Direction direction;
    private Square currentSquare;
    private int row;
    private int column;


    protected Eyeball(Direction direction, Square currentSquare, int row, int column) {
        super();
        this.direction = direction;
        this.currentSquare = currentSquare;
        this.row = row;
        this.column = column;
    }

    protected Direction getDirection() {
        return this.direction;
    }

    protected void setDirection(Direction direction) {
        this.direction = direction;
    }

    protected void setCurrentSquare(Square newSquare, int row, int column) {
        this.currentSquare = newSquare;
        this.row = row;
        this.column = column;
    }

    protected Shape getCurrentSquareShape() {
        return currentSquare.getShape();
    }

    protected Color getCurrentSquareColor() {
        return currentSquare.getColor();
    }

    protected int getRow() {
        return row;
    }

    protected int getColumn() {
        return column;
    }
}
