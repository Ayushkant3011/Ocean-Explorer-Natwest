package com.natwest.oceanexplorer.exception;

import com.natwest.oceanexplorer.model.Position;

/**
 * Thrown when the probe attempts to move into a position occupied by an obstacle.
 * The probe stays at its current position when this exception is raised.
 */
public class ObstacleEncounteredException extends RuntimeException {

    private final Position obstaclePos;

    public ObstacleEncounteredException(Position obstaclePos) {
        super("Obstacle encountered at " + obstaclePos + ". Probe has not moved.");
        this.obstaclePos = obstaclePos;
    }

    public Position getobstaclePos() {
        return obstaclePos;
    }
}
