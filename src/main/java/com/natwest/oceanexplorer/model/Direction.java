package com.natwest.oceanexplorer.model;

/**
 * Represents the cardinal direction the probe is facing.
 * Encapsulates all directional movement and rotation logic.
 */
public enum Direction {
    NORTH, EAST, SOUTH, WEST;

    /**
     * Returns the direction after turning 90 degrees clockwise (right).
     */
    public Direction turnRight() {
        return values()[(this.ordinal() + 1) % 4];
    }

    /**
     * Returns the direction after turning 90 degrees counter-clockwise (left).
     */
    public Direction turnLeft() {
        return values()[(this.ordinal() + 3) % 4];
    }

    /**
     * Returns the x-axis delta for a forward move in this direction.
     */
    public int deltaX() {
        return switch (this) {
            case EAST  ->  1;
            case WEST  -> -1;
            default    ->  0;
        };
    }

    /**
     * Returns the y-axis delta for a forward move in this direction.
     */
    public int deltaY() {
        return switch (this) {
            case NORTH ->  1;
            case SOUTH -> -1;
            default    ->  0;
        };
    }
}
