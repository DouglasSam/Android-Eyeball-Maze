package nz.ac.ara.sjd0364;

import androidx.annotation.NonNull;

import nz.ac.ara.sjd0364.model.enums.Direction;

public record Move(Direction direction, Coordinate coordinate, boolean wasGoal) {

    @NonNull
    @Override
    public String toString() {
        return direction + " " + coordinate.row() + " " + coordinate.column() + " " + wasGoal;
    }
}
