package com.natwest.oceanexplorer.exception;

import com.natwest.oceanexplorer.model.Position;

/**
 * Thrown when the probe attempts to move outside the defined grid boundaries.
 * The probe stays at its current position when this exception is raised.
 */
public class BoundaryExceededException extends RuntimeException {

    private final Position attemptedPosition;

    public BoundaryExceededException(Position attemptedPosition) {
        super("Move to " + attemptedPosition + " would exceed grid boundaries. Probe has not moved.");
        this.attemptedPosition = attemptedPosition;
    }

    public Position getAttemptedPosition() {
        return attemptedPosition;
    }
}
