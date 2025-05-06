package nz.ac.ara.sjd0364.model;

import nz.ac.ara.sjd0364.model.enums.Color;
import nz.ac.ara.sjd0364.model.enums.Direction;
import nz.ac.ara.sjd0364.model.enums.Message;
import nz.ac.ara.sjd0364.model.enums.Shape;
import nz.ac.ara.sjd0364.model.interfaces.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that holds all the interactions with the model.
 * @author sjd0364 - Samuel Douglas
 */
public class Game implements ILevelHolder, IGoalHolder, ISquareHolder, IEyeballHolder , IMoving {

    private final List<Level> levels = new ArrayList<>();
    private Level currentLevel;


    /**
     * Add a level to the game.
     * @param height The height of the level.
     * @param width The width of the level.
     */
    @Override
    public void addLevel(int height, int width) {
        currentLevel = new Level(height, width);
        levels.add(currentLevel);
    }

    /**
     * Gets the width of the current level.
     * @return The width of the current level.
     */
    @Override
    public int getLevelWidth() {
        return currentLevel.getWidth();
    }

    /**
     * Gets the height of the current level.
     * @return The height of the current level.
     */
    @Override
    public int getLevelHeight() {
        return currentLevel.getHeight();
    }

    /**
     * Sets the current level.
     * @param levelNumber The level number to set.
     */
    @Override
    public void setLevel(int levelNumber) {
        if (levelNumber < 0 || levelNumber >= levels.size()) {
            throw new IllegalArgumentException("Invalid level number");
        }
        currentLevel = levels.get(levelNumber);
    }

    /**
     * Add a goal to the current level
     * @param row The row to add the goal to.
     * @param column The column to add the goal to.
     */
    @Override
    public void addGoal(int row, int column) {
        currentLevel.addGoal(row, column);
    }

    /**
     * Gets the number of goals in the current level.
     * @return The number of goals in the current level.
     */
    @Override
    public int getGoalCount() {
        return currentLevel.getGoalCount();
    }

    /**
     * Checks to see if there is a goal at a point.
     * @param targetRow The row to check.
     * @param targetColumn The column to check.
     * @return True if there is a goal at the point.
     */
    @Override
    public boolean hasGoalAt(int targetRow, int targetColumn) {
        return currentLevel.hasGoalAt(targetRow, targetColumn);
    }

    /**
     * Gets the number of completed goals in the current level.
     * @return The number of completed goals in the current level.
     */
    @Override
    public int getCompletedGoalCount() {
        return currentLevel.getCompletedGoals();
    }

    /**
     * Gets the number of levels in the game.
     * @return The number of levels in the game.
     */
    @Override
    public int getLevelCount() {
        return levels.size();
    }

    /**
     * Add a square to the current level.
     * @param square The square to add.
     * @param row The row to add the square to.
     * @param column The column to add the square to.
     */
    @Override
    public void addSquare(Square square, int row, int column) {
        currentLevel.addSquare(square, row, column);
    }

    /**
     * Gets the color of a square at a point.
     * @param row The row to get the color from.
     * @param column The column to get the color from.
     * @return The color of the square.
     */
    @Override
    public Color getColorAt(int row, int column) {
        return currentLevel.getSquare(row, column).getColor();
    }

    /**
     * Gets the shape of a square at a point.
     * @param row The row to get the shape from.
     * @param column The column to get the shape from.
     * @return The shape of the square.
     */
    @Override
    public Shape getShapeAt(int row, int column) {
        return currentLevel.getSquare(row, column).getShape();
    }

    /**
     * Add an eyeball to the current level.
     * @param row The row to add the eyeball to.
     * @param column The column to add the eyeball to.
     * @param direction The direction the eyeball is facing.
     */
    @Override
    public void addEyeball(int row, int column, Direction direction) {
        currentLevel.addEyeball(row, column, direction);
    }

    /**
     * Gets the current row of the eyeball.
     * @return The current row of the eyeball.
     */
    @Override
    public int getEyeballRow() {
        return currentLevel.getEyeball().getRow();
    }

    /**
     * Gets the current column of the eyeball.
     * @return The current column of the eyeball.
     */
    @Override
    public int getEyeballColumn() {
        return currentLevel.getEyeball().getColumn();
    }

    /**
     * Gets the current direction of the eyeball.
     * @return The current direction of the eyeball.
     */
    @Override
    public Direction getEyeballDirection() {
        return currentLevel.getEyeball().getDirection();
    }

    /**
     * Checks if the eyeball can move to a point.
     * @param destinationRow The row to move to.
     * @param destinationColumn The column to move to.
     * @return True if the eyeball can move to the point.
     */
    @Override
    public boolean canMoveTo(int destinationRow, int destinationColumn) {
        return currentLevel.testMoveEyeball(destinationRow, destinationColumn) == Message.OK;
    }

    /**
     * Gets the message if the eyeball moves to a point.
     * @param destinationRow The row to move to.
     * @param destinationColumn The column to move to.
     * @return The message if the eyeball moves to the point.
     */
    @Override
    public Message messageIfMovingTo(int destinationRow, int destinationColumn) {
        return currentLevel.testMoveEyeball(destinationRow, destinationColumn);
    }

    /**
     * Checks if the direction of a move is valid for the current eyeball
     * @param destinationRow The row to test move to.
     * @param destinationColumn The column to test move to.
     * @return True if the direction is valid.
     */
    @Override
    public boolean isDirectionOK(int destinationRow, int destinationColumn) {
        return canMoveTo(destinationRow, destinationColumn);
    }

    /**
     * Gets the message if the direction of a move is invalid for the current eyeball.
     * @param destinationRow The row to test move to.
     * @param destinationColumn The column to test move to.
     * @return The message if the direction is invalid.
     */
    @Override
    public Message checkDirectionMessage(int destinationRow, int destinationColumn) {
        return messageIfMovingTo(destinationRow, destinationColumn);
    }

    /**
     * Checks if there is a blank square in the path to a point.
     * @param destinationRow The row to check.
     * @param destinationColumn The column to check.
     * @return True if there is a blank path to the point.
     */
    @Override
    public boolean hasBlankFreePathTo(int destinationRow, int destinationColumn) {
        return canMoveTo(destinationRow, destinationColumn);
    }

    /**
     * Gets the message if there is a blank square in the path to a point.
     * @param destinationRow The row to check.
     * @param destinationColumn The column to check.
     * @return The message if there is a blank path to the point.
     */
    @Override
    public Message checkMessageForBlankOnPathTo(int destinationRow, int destinationColumn) {
        return messageIfMovingTo(destinationRow, destinationColumn);
    }

    /**
     * Moves the eyeball to a point.
     * @param destinationRow The row to move to.
     * @param destinationColumn The column to move to.
     */
    @Override
    public void moveTo(int destinationRow, int destinationColumn) {
        currentLevel.moveEyeball(destinationRow, destinationColumn);
    }
}
