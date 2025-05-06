package nz.ac.ara.sjd0364.model;

/**
 * The position consisting of a row and column.
 * @author sjd0364 - Samuel Douglas
 */
public class Position {

    private int row;
    private int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
