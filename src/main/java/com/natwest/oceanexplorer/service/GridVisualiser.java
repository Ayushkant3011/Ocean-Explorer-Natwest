package com.natwest.oceanexplorer.service;

import com.natwest.oceanexplorer.model.Direction;
import com.natwest.oceanexplorer.model.Grid;
import com.natwest.oceanexplorer.model.Position;
import com.natwest.oceanexplorer.model.Probe;

import java.util.List;

public class GridVisualiser {

    private static final char OBSTACLE     = '#';
    private static final char OPEN_WATER   = '~';
    private static final char VISITED      = '.';

    private static final char PROBE_NORTH  = '^';
    private static final char PROBE_EAST   = '>';
    private static final char PROBE_SOUTH  = 'v';
    private static final char PROBE_WEST   = '<';

    private final Grid grid;

    public GridVisualiser(Grid grid) {
        this.grid = grid;
    }

    /**
     * Prints the current state of the grid with the probe's position and visited trail.
    **/
    public void print(Probe probe) {
        List<Position> visited = probe.getVisitedPositions();
        Position current       = probe.getPosition();

        int maxYLabel = String.valueOf(grid.getHeight() - 1).length();
        int maxXLabel = String.valueOf(grid.getWidth() - 1).length();
        int cellWidth = 2; //

        System.out.println();

        // Rows printed top-to-bottom (high Y first)
        for (int y = grid.getHeight() - 1; y >= 0; y--) {
            System.out.printf("%" + maxYLabel + "d |", y);
            for (int x = 0; x < grid.getWidth(); x++) {
                Position pos = new Position(x, y);
                char cell = cellChar(pos, current, probe.getDirection(), visited);
                System.out.printf(" %" + cellWidth + "c", cell);
            }
            System.out.println();
        }

        // Bottom border
        String indent = " ".repeat(maxYLabel + 2);
        System.out.print(indent);
        System.out.println("-".repeat(grid.getWidth() * (cellWidth + 1) + 1));

        // X-axis labels
        System.out.print(indent);
        for (int x = 0; x < grid.getWidth(); x++) {
            System.out.printf(" %" + cellWidth + "d", x);
        }
        System.out.println("  X");
        System.out.println();
    }

    // Private helpers

    private char cellChar(Position pos, Position probePos, Direction facing, List<Position> visited) {
        if (pos.equals(probePos)) {
            return probeGlyph(facing);
        }
        if (grid.hasObstacleAt(pos)) {
            return OBSTACLE;
        }
        if (visited.contains(pos)) {
            return VISITED;
        }
        return OPEN_WATER;
    }

    private char probeGlyph(Direction facing) {
        return switch (facing) {
            case NORTH -> PROBE_NORTH;
            case EAST  -> PROBE_EAST;
            case SOUTH -> PROBE_SOUTH;
            case WEST  -> PROBE_WEST;
        };
    }
}
