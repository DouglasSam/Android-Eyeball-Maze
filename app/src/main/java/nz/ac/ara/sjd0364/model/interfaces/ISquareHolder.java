package nz.ac.ara.sjd0364.model.interfaces;

import nz.ac.ara.sjd0364.model.enums.Color;
import nz.ac.ara.sjd0364.model.enums.Shape;

public interface ISquareHolder {
    public void addSquare(Square square, int row, int column);
    public Color getColorAt(int row, int column);
    public Shape getShapeAt(int row, int column);
}