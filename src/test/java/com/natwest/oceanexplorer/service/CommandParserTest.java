package com.natwest.oceanexplorer.service;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

class CommandParserTest {

    /**
     * Runs a CommandParser session with the given stdin content and captures all stdout.
     */
    private String run(String stdinContent) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(stdinContent.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            new CommandParser(scanner).run();
        } finally {
            System.setOut(original);
        }
        return out.toString();
    }

    private String basicSetup(String commands) {
        // 5x5 grid, 0 obstacles, probe at (2,2) NORTH
        return "5 5\n0\n2 2 NORTH\n" + commands + "\nquit\n";
    }

    @Test
    void sessionStartsAndEndsWithSummary() {
        String output = run(basicSetup("F"));
        assertThat(output).contains("Probe Summary");
    }

    @Test
    void forwardCommandMovesProbeAndConfirms() {
        String output = run(basicSetup("F"));
        assertThat(output).contains("(2, 3)");
    }

    @Test
    void obstacleBlocksMovementAndReportsWarning() {
        // 5x5 grid, obstacle at (2,3), probe at (2,2) NORTH
        String input = "5 5\n1\n2 3\n2 2 NORTH\nF\nquit\n";
        String output = run(input);
        assertThat(output).containsIgnoringCase("obstacle");
    }

    @Test
    void boundaryBlocksMovementAndReportsWarning() {
        // probe at top edge trying to go north
        String input = "5 5\n0\n2 4 NORTH\nF\nquit\n";
        String output = run(input);
        assertThat(output).containsIgnoringCase("boundary");
    }

    @Test
    void summaryCommandPrintsSummary() {
        String output = run(basicSetup("FF\nsummary"));
        // Summary should appear at least twice (once for 'summary' command, once at session end)
        int count = output.split("Probe Summary", -1).length - 1;
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    void unknownCommandCharacterReportsError() {
        String output = run(basicSetup("X"));
        assertThat(output).containsIgnoringCase("error").containsIgnoringCase("Unknown command");
    }

    @Test
    void blankLinesAreIgnored() {
        String input = "5 5\n0\n2 2 NORTH\n\n\nF\nquit\n";
        String output = run(input);
        assertThat(output).contains("(2, 3)");
    }

    @Test
    void quitEndsSession() {
        String output = run(basicSetup("quit"));
        assertThat(output).contains("Session ended");
    }

    @Test
    void exitAlsoEndsSession() {
        String output = run(basicSetup("exit"));
        assertThat(output).contains("Session ended");
    }

    @Test
    void multipleCommandSequencesAreExecutedInOrder() {
        // FF north to (2,4), then R to face EAST — probe should now be at (2,4) facing EAST
        String output = run(basicSetup("FF\nR"));
        assertThat(output).contains("(2, 4)").contains("EAST");
    }
}
