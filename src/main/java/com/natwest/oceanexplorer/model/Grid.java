package com.natwest.oceanexplorer.model;

import com.natwest.oceanexplorer.exception.InvalidGridException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the ocean floor grid.
 *
 * The grid spans from (0, 0) at the bottom-left to (width-1, height-1) at the top-right.
 * Obstacles can be placed at any valid grid position.
 */
public class Grid {

    private final int width;
    private final int height;
    private final Set<Position> obstacles;

    /**
     * Creates a grid with no obstacles.
     *
     * @param width  number of columns (must be >= 1)
     * @param height number of rows (must be >= 1)
     */
    public Grid(int width, int height) {
        this(width, height, Collections.emptySet());
    }

    /**
     * Creates a grid with pre-defined obstacles.
     *
     * @param width     number of columns (must be >= 1)
     * @param height    number of rows (must be >= 1)
     * @param obstacles set of positions that are blocked
     * @throws InvalidGridException if dimensions are invalid or an obstacle is out of bounds
     */
    public Grid(int width, int height, Set<Position> obstacles) {
        if (width < 1 || height < 1) {
            throw new InvalidGridException("Grid dimensions must be at least 1x1, got: " + width + "x" + height);
        }
        this.width = width;
        this.height = height;
        this.obstacles = new HashSet<>(obstacles);

        for (Position obstacle : this.obstacles) {
            if (!isWithinBounds(obstacle)) {
                throw new InvalidGridException("Obstacle at " + obstacle + " is outside the grid bounds (" + width + "x" + height + ").");
            }
        }
    }

    /**
     * Returns true if the given position is within the grid boundaries.
     */
    public boolean isWithinBounds(Position position) {
        return position.getX() >= 0
                && position.getX() < width
                && position.getY() >= 0
                && position.getY() < height;
    }

    /**
     * Returns true if the given position contains an obstacle.
     */
    public boolean hasObstacleAt(Position position) {
        return obstacles.contains(position);
    }

    /**
     * Returns true if the position is within bounds AND not an obstacle.
     */
    public boolean isNavigable(Position position) {
        return isWithinBounds(position) && !hasObstacleAt(position);
    }

    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    public Set<Position> getObstacles() {
        return Collections.unmodifiableSet(obstacles);
    }
}
