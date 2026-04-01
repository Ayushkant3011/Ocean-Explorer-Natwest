package com.natwest.oceanexplorer.exception;

import com.natwest.oceanexplorer.model.Position;

/**
 * Thrown when the probe attempts to move into a position occupied by an obstacle.
 * The probe stays at its current position when this exception is raised.
 */
public class ObstacleEncounteredException extends RuntimeException {

    private final Position blockedPosition;

    public ObstacleEncounteredException(Position blockedPosition) {
        super("Obstacle encountered at " + blockedPosition + ". Probe has not moved.");
        this.blockedPosition = blockedPosition;
    }

    public Position getBlockedPosition() {
        return blockedPosition;
    }
}
