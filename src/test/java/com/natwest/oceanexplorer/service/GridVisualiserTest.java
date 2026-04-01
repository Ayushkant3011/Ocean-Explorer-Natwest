package com.natwest.oceanexplorer.service;

import com.natwest.oceanexplorer.model.Direction;
import com.natwest.oceanexplorer.model.Grid;
import com.natwest.oceanexplorer.model.Position;
import com.natwest.oceanexplorer.model.Probe;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GridVisualiserTest {

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return out.toString();
    }

    @Test
    void probeGlyphFacingNorthIsCaretSymbol() {
        Grid grid = new Grid(3, 3);
        Probe probe = new Probe(new Position(1, 1), Direction.NORTH);
        GridVisualiser vis = new GridVisualiser(grid);

        String output = captureOutput(() -> vis.print(probe));
        assertThat(output).contains("^");
    }

    @Test
    void probeGlyphFacingEastIsGreaterThan() {
        Grid grid = new Grid(3, 3);
        Probe probe = new Probe(new Position(1, 1), Direction.EAST);
        GridVisualiser vis = new GridVisualiser(grid);

        String output = captureOutput(() -> vis.print(probe));
        assertThat(output).contains(">");
    }

    @Test
    void probeGlyphFacingSouthIsLowercaseV() {
        Grid grid = new Grid(3, 3);
        Probe probe = new Probe(new Position(1, 1), Direction.SOUTH);
        GridVisualiser vis = new GridVisualiser(grid);

        String output = captureOutput(() -> vis.print(probe));
        assertThat(output).contains("v");
    }

    @Test
    void probeGlyphFacingWestIsLessThan() {
        Grid grid = new Grid(3, 3);
        Probe probe = new Probe(new Position(1, 1), Direction.WEST);
        GridVisualiser vis = new GridVisualiser(grid);

        String output = captureOutput(() -> vis.print(probe));
        assertThat(output).contains("<");
    }

    @Test
    void obstacleIsRenderedAsHash() {
        Grid grid = new Grid(3, 3, Set.of(new Position(0, 0)));
        Probe probe = new Probe(new Position(2, 2), Direction.NORTH);
        GridVisualiser vis = new GridVisualiser(grid);

        String output = captureOutput(() -> vis.print(probe));
        assertThat(output).contains("#");
    }

    @Test
    void visitedCellIsRenderedAsDot() {
        Grid grid = new Grid(5, 5);
        Probe probe = new Probe(new Position(0, 0), Direction.NORTH);
        ProbeController controller = new ProbeController(grid, probe);
        controller.execute("F"); // visited (0,0) and (0,1), now at (0,1)

        GridVisualiser vis = new GridVisualiser(grid);
        String output = captureOutput(() -> vis.print(probe));
        assertThat(output).contains(".");
    }

    @Test
    void openWaterIsRenderedAsTilde() {
        Grid grid = new Grid(3, 3);
        Probe probe = new Probe(new Position(0, 0), Direction.NORTH);
        GridVisualiser vis = new GridVisualiser(grid);

        String output = captureOutput(() -> vis.print(probe));
        assertThat(output).contains("~");
    }

    @Test
    void xAxisLabelAppearsInOutput() {
        Grid grid = new Grid(3, 3);
        Probe probe = new Probe(new Position(0, 0), Direction.NORTH);
        GridVisualiser vis = new GridVisualiser(grid);

        String output = captureOutput(() -> vis.print(probe));
        assertThat(output).contains("X");
    }

    @Test
    void yAxisLabelsArePresent() {
        Grid grid = new Grid(3, 3);
        Probe probe = new Probe(new Position(0, 0), Direction.NORTH);
        GridVisualiser vis = new GridVisualiser(grid);

        String output = captureOutput(() -> vis.print(probe));
        // All row labels 0–2 should be present
        assertThat(output).contains("0").contains("1").contains("2");
    }
}
