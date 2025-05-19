package nz.ac.ara.sjd0364.model;

import java.util.HashSet;
import java.util.Set;

import nz.ac.ara.sjd0364.model.enums.Direction;
import nz.ac.ara.sjd0364.model.enums.Message;
import nz.ac.ara.sjd0364.model.interfaces.Square;

/**
 * A level that is played in as well as the level logic
 * @author sjd0364 - Samuel Douglas
 */
public class Level {

    private final Square[][] board;
    private Eyeball eyeball;
    private final Set<Goal> goals;
    private int totalGoals;
    private boolean removeGoal;

    protected Level(int height, int width) {
        board = new Square[height][width];
        this.goals = new HashSet<>();
    }

    protected int getWidth() {
        return board[0].length;
    }

    protected int getHeight() {
        return board.length;
    }

    /**
     * Add an eyeball to the level. Only one eyeball can be added.
     * @param row The row to add the eyeball to.
     * @param column The column to add the eyeball to.
     * @param direction The direction the eyeball is facing.
     */
    protected void addEyeball(int row, int column , Direction direction) {
        testCoordinatesBoundaries(row, column);
        removeGoal = false; // Reset the remove goal flag just in case eyeball is added/moved in the middle of a game
        eyeball = new Eyeball(direction, getSquare(row, column), row, column);
    }

    /**
     * Test if the coordinates are within the boundaries of the level.
     * @param row The row to test.
     * @param column The column to test.
     * @throws IllegalArgumentException If the coordinates are not within the boundaries of the level.
     */
    private void testCoordinatesBoundaries(int row, int column) throws IllegalArgumentException {
        if (row < 0 || row >= getHeight() || column < 0 || column >= getWidth()) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
    }

    /**
     * Adds a goal to the level. There can be multiple goals.
     * @param row The row to add the goal to.
     * @param column The column to add the goal to.
     */
    protected void addGoal(int row, int column) {
        testCoordinatesBoundaries(row, column);
        goals.add(new Goal(row, column));
        totalGoals++;
    }

    /**
     * Gets the total number of goals in the level.
     * @return The total number of goals in the level.
     */
    protected int getGoalCount() {
        return goals.size();
    }

    /**
     * Gets the number of completed goals in the level.
     * @return The number of completed goals in the level.
     */
    protected int getCompletedGoals() {
        return totalGoals - goals.size();
    }

    /**
     * Gets the goal at a point.
     * @param row The row the goal is at.
     * @param column The column the goal is at.
     * @return The goal if there is one, otherwise null.
     */
    private Goal getGoal(int row, int column) {
        for (Goal goal : goals) {
            if (goal.row() == row && goal.column() == column) {
                return goal;
            }
        }
        return null;
    }

    /**
     * Check if there is a goal at a point.
     * @param row The row to check
     * @param column The column to check
     * @return True if there is a goal there.
     */
    protected boolean hasGoalAt(int row, int column) {
        try {
            testCoordinatesBoundaries(row, column);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return getGoal(row, column) != null;
    }

    /**
     * Add a square to the level.
     * @param square The square to add.
     * @param row The row to add the square to.
     * @param column The column to add the square to.
     */
    protected void addSquare(Square square, int row, int column) {
        testCoordinatesBoundaries(row, column);
        board[row][column] = square;
    }

    /**
     * Get the square at a point.
     * @param row The row to get the square from.
     * @param column The column to get the square from.
     * @return The square at the point.
     */
    protected Square getSquare(int row, int column) {
        testCoordinatesBoundaries(row, column);
        return board[row][column];
    }

    /**
     * Gets the Eyeball
     * @return The Eyeball
     */
    protected Eyeball getEyeball() {
        return eyeball;
    }

    /**
     * Test if the Eyeball can move to a point.
     * @param row The row to test move to.
     * @param column The column to test move to.
     * @return True if the Eyeball can move to the point.
     */
    protected Message testMoveEyeball(int row, int column) {
        testCoordinatesBoundaries(row, column);
        Square destinationSquare = getSquare(row, column);
        if (destinationSquare instanceof BlankSquare) {
//            Cannot move to blank square
            return Message.MOVING_OVER_BLANK;
        }
        int diffRow = row - eyeball.getRow();
        int diffColumn = column - eyeball.getColumn();
        if (diffRow != 0 && diffColumn != 0) {
//            Cannot move diagonally
            return Message.MOVING_DIAGONALLY;
        }
        if (eyeball.getCurrentSquareColor() != destinationSquare.getColor() && eyeball.getCurrentSquareShape() != destinationSquare.getShape()) {
//            Cannot move to square with different color and shape
            return Message.DIFFERENT_SHAPE_OR_COLOR;
        }
        if (eyeball.getDirection().getOpposite() == getMoveDirection(row, column)) {
//            Cannot move backwards
            return Message.BACKWARDS_MOVE;
        }

        if (diffRow != 0) {
            for (int i = Math.min(eyeball.getRow(), row); i < Math.max(eyeball.getRow(), row); i++) {
                if (board[i][column] instanceof BlankSquare) {
                    return Message.MOVING_OVER_BLANK;
                }
            }
        } else if (diffColumn != 0) {
            for (int i = Math.min(eyeball.getColumn(), column); i < Math.max(eyeball.getColumn(), column); i++) {
                if (board[row][i] instanceof BlankSquare) {
                    return Message.MOVING_OVER_BLANK;
                }
            }
        }

        return Message.OK;
    }

    /**
     * Get the direction the Eyeball will move in giving a point to move to.
     * @param row The row to move to.
     * @param column The column to move to.
     * @return The direction the Eyeball should move in.
     */
    private Direction getMoveDirection(int row, int column) {
        int diffRow = row - eyeball.getRow();
        int diffColumn = column - eyeball.getColumn();
        Direction movingDirection = null;
        if (diffColumn > 0) {
            movingDirection = Direction.RIGHT;
        } else if (diffColumn < 0) {
            movingDirection = Direction.LEFT;
        } else if (diffRow > 0) {
            movingDirection = Direction.DOWN;
        } else if (diffRow < 0) {
            movingDirection = Direction.UP;
        }
        return movingDirection;
    }

    /**
     * Move the Eyeball to a point.
     * @param row The row to move to.
     * @param column The column to move to.
     */
    protected void moveEyeball(int row, int column) {
        Message message = testMoveEyeball(row, column);
        if (message == Message.OK) {
            if (removeGoal) {
                board[eyeball.getRow()][eyeball.getColumn()] = new BlankSquare();
                removeGoal = false;
            }
            eyeball.setDirection(getMoveDirection(row, column));
            eyeball.setCurrentSquare(getSquare(row, column), row, column);
            if (hasGoalAt(row, column)) {
                goals.remove(getGoal(row, column));
//                level[row][column] = new BlankSquare();
                removeGoal = true;
            }
        }
    }
}
