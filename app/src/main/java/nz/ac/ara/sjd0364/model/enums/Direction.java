package nz.ac.ara.sjd0364.model.enums;

/**
 * Enum for direction on the game board.
 */
public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    /**
     * Get the opposite direction.
     * @return the opposite direction
     */
//    public Direction getOpposite() {
//        return switch (this) {
//            case UP -> DOWN;
//            case DOWN -> UP;
//            case LEFT -> RIGHT;
//            case RIGHT -> LEFT;
//        };
//    }

    public Direction getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

}

