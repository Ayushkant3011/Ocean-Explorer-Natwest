package com.natwest.oceanexplorer.service;

import com.natwest.oceanexplorer.exception.BoundaryExceededException;
import com.natwest.oceanexplorer.exception.ObstacleEncounteredException;
import com.natwest.oceanexplorer.model.Command;
import com.natwest.oceanexplorer.model.Direction;
import com.natwest.oceanexplorer.model.Grid;
import com.natwest.oceanexplorer.model.Position;
import com.natwest.oceanexplorer.model.Probe;

import java.util.List;

/**
 * Core API for controlling the submersible probe.
 *
 * Processes command sequences and enforces grid constraints (boundaries and obstacles).
 * If a move command results in a boundary or obstacle violation, the probe remains
 * in place and the appropriate exception is thrown — subsequent commands in the
 * same sequence are NOT executed after an exception.
 *
 * <pre>
 * Usage example:
 *   Grid grid = new Grid(10, 10);
 *   Probe probe = new Probe(new Position(0, 0), Direction.NORTH);
 *   ProbeController controller = new ProbeController(grid, probe);
 *   controller.execute("FFRFF");
 *   controller.printSummary();
 * </pre>
 */
public class ProbeController {

    private final Grid grid;
    private final Probe probe;

    /**
     * @param grid  the ocean floor grid containing dimensions and obstacles
     * @param probe the probe with its initial position and direction
     * @throws IllegalArgumentException if the probe's starting position is invalid
     */
    public ProbeController(Grid grid, Probe probe) {
        this.grid  = grid;
        this.probe = probe;
        validateStartingPosition(probe.getPosition());
    }

    /**
     * Executes a sequence of commands provided as a string (e.g. "FFLBR").
     * Each character is parsed as a {@link Command} and applied in order.
     *
     * @param commandString the string of command characters (case-insensitive)
     * @throws IllegalArgumentException      if an unknown command character is encountered
     * @throws BoundaryExceededException     if a move would take the probe off the grid
     * @throws ObstacleEncounteredException  if a move would take the probe into an obstacle
     */
    public void execute(String commandString) {
        if (commandString == null || commandString.isBlank()) {
            return;
        }
        for (char c : commandString.toCharArray()) {
            Command command = Command.from(c);
            apply(command);
        }
    }

    /**
     * Executes a list of {@link Command} objects in order.
     *
     * @param commands the list of commands to execute
     * @throws BoundaryExceededException    if a move would take the probe off the grid
     * @throws ObstacleEncounteredException if a move would take the probe into an obstacle
     */
    public void execute(List<Command> commands) {
        for (Command command : commands) {
            apply(command);
        }
    }

    /**
     * Prints a formatted summary of: current position, facing direction,
     * and the full ordered list of coordinates visited during the session.
     */
    public void printSummary() {
        System.out.println("=== Ocean Explorer — Probe Summary ===");
        System.out.println("Current position : " + probe.getPosition());
        System.out.println("Facing           : " + probe.getDirection());
        System.out.println("Coordinates visited (" + probe.getVisitedPositions().size() + "):");
        probe.getVisitedPositions().forEach(p -> System.out.println("  -> " + p));
        System.out.println("======================================");
    }

    /**
     * Returns the current state of the probe (position and direction).
     */
    public Probe getProbe() {
        return probe;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

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
        Position proposed = current.translate(dx, dy);

        if (!grid.isWithinBounds(proposed)) {
            throw new BoundaryExceededException(proposed);
        }
        if (grid.hasObstacleAt(proposed)) {
            throw new ObstacleEncounteredException(proposed);
        }

        probe.moveTo(proposed);
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
