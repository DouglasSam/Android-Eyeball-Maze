package nz.ac.ara.sjd0364;

import nz.ac.ara.sjd0364.model.enums.Direction;

public record Move(Direction direction, PlayableSquareView squareView, boolean wasGoal) {

}
