package com.natwest.oceanexplorer.service;

import com.natwest.oceanexplorer.exception.BoundaryExceededException;
import com.natwest.oceanexplorer.exception.ObstacleEncounteredException;
import com.natwest.oceanexplorer.model.Command;
import com.natwest.oceanexplorer.model.Direction;
import com.natwest.oceanexplorer.model.Grid;
import com.natwest.oceanexplorer.model.Position;
import com.natwest.oceanexplorer.model.Probe;

import java.util.List;

public class ProbeController {

    private final Grid grid;
    private final Probe probe;


    public ProbeController(Grid grid, Probe probe) {
        this.grid  = grid;
        this.probe = probe;
        validateStartingPosition(probe.getPosition());
    }

   
    public void execute(String input) {
        if (input == null || input.isBlank()) {
            return;
        }
        for (char c : input.toCharArray()) {
            Command command = Command.from(c);
            apply(command);
        }
    }

    
    public void execute(List<Command> commands) {
        for (Command command : commands) {
            apply(command);
        }
    }

    public void printSummary() {
        System.out.println("=== Ocean Explorer — Probe Summary ===");
        System.out.println("Current position : " + probe.getPosition());
        System.out.println("Facing           : " + probe.getDirection());
        System.out.println("Coordinates visited (" + probe.getVisitedPositions().size() + "):");
        probe.getVisitedPositions().forEach(p -> System.out.println("  -> " + p));
    }

    // Returns the current state of the probe (position and direction).
    
    public Probe getProbe() {
        return probe;
    }

    // Private helpers

    private void apply(Command command) {
        switch (command) {
            case F -> attemptMove( probe.getDirection().deltaX(),  probe.getDirection().deltaY());
            case B -> attemptMove(-probe.getDirection().deltaX(), -probe.getDirection().deltaY());
            case L -> probe.face(probe.getDirection().turnLeft());
            case R -> probe.face(probe.getDirection().turnRight());
        }
    }

    private void attemptMove(int dx, int dy) {
        Position current  = probe.getPosition();
        Position nextPos = current.translate(dx, dy);

        if (!grid.isWithinBounds(nextPos)) {
            throw new BoundaryExceededException(nextPos);
        }
        if (grid.hasObstacleAt(nextPos)) {
            throw new ObstacleEncounteredException(nextPos);
        }

        probe.moveTo(nextPos);
    }

    private void validateStartingPosition(Position position) {
        if (!grid.isWithinBounds(position)) {
            throw new IllegalArgumentException(
                    "Starting position " + position + " is outside the grid bounds.");
        }
        if (grid.hasObstacleAt(position)) {
            throw new IllegalArgumentException(
                    "Starting position " + position + " is occupied by an obstacle.");
        }
    }
}
