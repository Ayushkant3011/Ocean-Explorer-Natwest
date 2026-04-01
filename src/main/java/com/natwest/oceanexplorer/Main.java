package com.natwest.oceanexplorer;

import com.natwest.oceanexplorer.exception.BoundaryExceededException;
import com.natwest.oceanexplorer.exception.ObstacleEncounteredException;
import com.natwest.oceanexplorer.model.Direction;
import com.natwest.oceanexplorer.model.Grid;
import com.natwest.oceanexplorer.model.Position;
import com.natwest.oceanexplorer.model.Probe;
import com.natwest.oceanexplorer.service.CommandParser;
import com.natwest.oceanexplorer.service.GridVisualiser;
import com.natwest.oceanexplorer.service.ProbeController;

import java.util.Set;

/**
 * Entry point for the Ocean Explorer application.
 *
 * Run with no arguments for the scripted demo.
 * Run with --interactive (or -i) for the interactive stdin session.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && (args[0].equals("--interactive") || args[0].equals("-i"))) {
            CommandParser.startInteractive();
        } else {
            runDemo();
        }
    }

    // -------------------------------------------------------------------------
    // Scripted demo
    // -------------------------------------------------------------------------

    private static void runDemo() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       Ocean Explorer — Demo          ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println();

        Grid grid = new Grid(8, 8, Set.of(
                new Position(3, 4),
                new Position(3, 5),
                new Position(5, 2)
        ));

        Probe probe = new Probe(new Position(1, 1), Direction.NORTH);
        ProbeController controller = new ProbeController(grid, probe);
        GridVisualiser visualiser = new GridVisualiser(grid);

        System.out.println("Grid: 8x8");
        System.out.println("Obstacles: (3,4)  (3,5)  (5,2)");
        System.out.println("Start:     (1,1)  facing NORTH");
        System.out.println();

        visualiser.print(probe);

        runStep(controller, visualiser, probe, "FF",   "Move north twice");
        runStep(controller, visualiser, probe, "R",    "Turn right (now facing EAST)");
        runStep(controller, visualiser, probe, "FFFF", "Move east four steps");
        runStep(controller, visualiser, probe, "R",    "Turn right (now facing SOUTH)");
        runStep(controller, visualiser, probe, "FF",   "Move south twice — will hit obstacle at (5,2)");

        System.out.println();
        controller.printSummary();
    }

    private static void runStep(ProbeController controller, GridVisualiser visualiser,
                                Probe probe, String commands, String description) {
        System.out.println("▶ " + description + "  [" + commands + "]");
        try {
            controller.execute(commands);
        } catch (BoundaryExceededException e) {
            System.out.println("  [BOUNDARY] " + e.getMessage());
        } catch (ObstacleEncounteredException e) {
            System.out.println("  [OBSTACLE] " + e.getMessage());
        }
        visualiser.print(probe);
    }
}
