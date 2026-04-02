package com.natwest.oceanexplorer.service;

import com.natwest.oceanexplorer.exception.BoundaryExceededException;
import com.natwest.oceanexplorer.exception.ObstacleEncounteredException;
import com.natwest.oceanexplorer.model.Direction;
import com.natwest.oceanexplorer.model.Grid;
import com.natwest.oceanexplorer.model.Position;
import com.natwest.oceanexplorer.model.Probe;

import java.util.Scanner;
import java.util.Set;

public class CommandParser {

    private final Scanner scanner;

    public CommandParser(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Runs the interactive session from the provided scanner.
     */
    public void run() {
        System.out.println("=== Ocean Explorer ===");

        Grid grid = readGrid();
        GridVisualiser visualiser = new GridVisualiser(grid);
        Probe probe = readProbeStart(grid);
        ProbeController controller = new ProbeController(grid, probe);

        System.out.println("\nGrid initialised (" + grid.getWidth() + "x" + grid.getHeight() + "). " +
                grid.getObstacles().size() + " obstacle(s) placed.");
        System.out.println("Probe at " + probe.getPosition() + " facing " + probe.getDirection());
        visualiser.print(probe);

        System.out.println("\nEnter command sequences (e.g. FFRFF). Type 'summary' or 'quit' to finish.\n");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();

            if (input.isBlank()) continue;

            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                break;
            }

            if (input.equalsIgnoreCase("summary")) {
                controller.printSummary();
                continue;
            }

            try {
                controller.execute(input);
                System.out.println("OK — " + probe.getPosition() + " facing " + probe.getDirection());
                visualiser.print(probe);
            } catch (BoundaryExceededException e) {
                System.out.println("[BOUNDARY] " + e.getMessage());
            } catch (ObstacleEncounteredException e) {
                System.out.println("[OBSTACLE] " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }

        System.out.println("\nSession ended.");
        controller.printSummary();
    }

    // Private helpers

    private Grid readGrid() {
        System.out.print("Enter grid dimensions (width height): ");
        int width  = scanner.nextInt();
        int height = scanner.nextInt();

        System.out.print("Enter number of obstacles: ");
        int obstacleCount = scanner.nextInt();

        Set<Position> obstacles = new java.util.HashSet<>();
        for (int i = 0; i < obstacleCount; i++) {
            System.out.print("  Obstacle " + (i + 1) + " (x y): ");
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            obstacles.add(new Position(x, y));
        }

        scanner.nextLine(); // consume trailing newline after last int
        return new Grid(width, height, obstacles);
    }

    private Probe readProbeStart(Grid grid) {
        System.out.print("Enter probe start (x y DIRECTION — e.g. 0 0 NORTH): ");
        int x         = scanner.nextInt();
        int y         = scanner.nextInt();
        String dirStr = scanner.next().toUpperCase();
        scanner.nextLine();

        Direction direction;
        try {
            direction = Direction.valueOf(dirStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unknown direction '" + dirStr + "'. Use NORTH, SOUTH, EAST, or WEST.");
        }

        return new Probe(new Position(x, y), direction);
    }

    public static void startInteractive() {
        new CommandParser(new Scanner(System.in)).run();
    }
}
