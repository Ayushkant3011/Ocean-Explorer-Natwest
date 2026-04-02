package com.natwest.oceanexplorer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;


public class Probe {

    private Position position;
    private Direction direction;
    private final SequencedSet<Position> visitedPositions;

    public Probe(Position startPosition, Direction startDirection) {
        this.position  = startPosition;
        this.direction = startDirection;
        this.visitedPositions = new LinkedHashSet<>();
        this.visitedPositions.add(startPosition);
    }

    /**
     * Moves the probe to the given position and records it as visited.
     */
    public void moveTo(Position newPosition) {
        this.position = newPosition;
        visitedPositions.add(newPosition);
    }

    /**
     * Rotates the probe to the given direction (no movement).
     */
    public void face(Direction newDirection) {
        this.direction = newDirection;
    }

    public Position getPosition()  { return position; }
    public Direction getDirection() { return direction; }

    /**
     * Returns all unique positions visited by the probe, in the order they were first visited.
     */
    public List<Position> getVisitedPositions() {
        return Collections.unmodifiableList(new ArrayList<>(visitedPositions));
    }

    @Override
    public String toString() {
        return "Probe{position=" + position + ", facing=" + direction + "}";
    }
}
