package com.natwest.oceanexplorer.service;

import com.natwest.oceanexplorer.exception.BoundaryExceededException;
import com.natwest.oceanexplorer.exception.ObstacleEncounteredException;
import com.natwest.oceanexplorer.model.Command;
import com.natwest.oceanexplorer.model.Direction;
import com.natwest.oceanexplorer.model.Grid;
import com.natwest.oceanexplorer.model.Position;
import com.natwest.oceanexplorer.model.Probe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProbeControllerTest {

    private Grid grid;

    @BeforeEach
    void setUp() {
        grid = new Grid(10, 10);
    }

    private ProbeController controller(int x, int y, Direction direction) {
        return new ProbeController(grid, new Probe(new Position(x, y), direction));
    }

    // -------------------------------------------------------------------------
    // Forward movement
    // -------------------------------------------------------------------------

    @Nested
    class ForwardMovement {

        @Test
        void movingForwardNorthIncreasesY() {
            ProbeController c = controller(5, 5, Direction.NORTH);
            c.execute("F");
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(5, 6));
        }

        @Test
        void movingForwardSouthDecreasesY() {
            ProbeController c = controller(5, 5, Direction.SOUTH);
            c.execute("F");
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(5, 4));
        }

        @Test
        void movingForwardEastIncreasesX() {
            ProbeController c = controller(5, 5, Direction.EAST);
            c.execute("F");
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(6, 5));
        }

        @Test
        void movingForwardWestDecreasesX() {
            ProbeController c = controller(5, 5, Direction.WEST);
            c.execute("F");
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(4, 5));
        }

        @Test
        void multipleForwardMovesTraversePath() {
            ProbeController c = controller(0, 0, Direction.NORTH);
            c.execute("FFF");
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(0, 3));
        }
    }

    // -------------------------------------------------------------------------
    // Backward movement
    // -------------------------------------------------------------------------

    @Nested
    class BackwardMovement {

        @Test
        void movingBackwardWhileFacingNorthDecreasesY() {
            ProbeController c = controller(5, 5, Direction.NORTH);
            c.execute("B");
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(5, 4));
        }

        @Test
        void movingBackwardWhileFacingEastDecreasesX() {
            ProbeController c = controller(5, 5, Direction.EAST);
            c.execute("B");
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(4, 5));
        }
    }

    // -------------------------------------------------------------------------
    // Turning
    // -------------------------------------------------------------------------

    @Nested
    class Turning {

        @Test
        void turningRightChangesDirectionButNotPosition() {
            ProbeController c = controller(5, 5, Direction.NORTH);
            c.execute("R");
            assertThat(c.getProbe().getDirection()).isEqualTo(Direction.EAST);
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(5, 5));
        }

        @Test
        void turningLeftChangesDirectionButNotPosition() {
            ProbeController c = controller(5, 5, Direction.NORTH);
            c.execute("L");
            assertThat(c.getProbe().getDirection()).isEqualTo(Direction.WEST);
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(5, 5));
        }

        @Test
        void combinedTurnAndMoveFollowsNewDirection() {
            ProbeController c = controller(5, 5, Direction.NORTH);
            c.execute("RF"); // face EAST, move to (6,5)
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(6, 5));
            assertThat(c.getProbe().getDirection()).isEqualTo(Direction.EAST);
        }
    }

    // -------------------------------------------------------------------------
    // Boundary enforcement
    // -------------------------------------------------------------------------

    @Nested
    class BoundaryEnforcement {

        @Test
        void movingOffNorthBoundaryThrowsException() {
            ProbeController c = controller(5, 9, Direction.NORTH);
            assertThatThrownBy(() -> c.execute("F"))
                    .isInstanceOf(BoundaryExceededException.class);
        }

        @Test
        void movingOffSouthBoundaryThrowsException() {
            ProbeController c = controller(5, 0, Direction.SOUTH);
            assertThatThrownBy(() -> c.execute("F"))
                    .isInstanceOf(BoundaryExceededException.class);
        }

        @Test
        void movingOffEastBoundaryThrowsException() {
            ProbeController c = controller(9, 5, Direction.EAST);
            assertThatThrownBy(() -> c.execute("F"))
                    .isInstanceOf(BoundaryExceededException.class);
        }

        @Test
        void movingOffWestBoundaryThrowsException() {
            ProbeController c = controller(0, 5, Direction.WEST);
            assertThatThrownBy(() -> c.execute("F"))
                    .isInstanceOf(BoundaryExceededException.class);
        }

        @Test
        void probePositionUnchangedAfterBoundaryViolation() {
            ProbeController c = controller(0, 0, Direction.SOUTH);
            assertThatThrownBy(() -> c.execute("F"));
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(0, 0));
        }

        @Test
        void subsequentCommandsNotExecutedAfterBoundaryException() {
            // "FF" — first F goes to y=9 (fine), second F hits y=10 (boundary)
            ProbeController c = controller(5, 8, Direction.NORTH);
            assertThatThrownBy(() -> c.execute("FF"));
            // Only first F executed; at y=9 when exception was thrown
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(5, 9));
        }

        @Test
        void startingOutsideGridThrowsException() {
            assertThatThrownBy(() -> new ProbeController(grid, new Probe(new Position(15, 15), Direction.NORTH)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -------------------------------------------------------------------------
    // Obstacle avoidance
    // -------------------------------------------------------------------------

    @Nested
    class ObstacleAvoidance {

        @Test
        void movingIntoObstacleThrowsException() {
            Grid gridWithObstacle = new Grid(10, 10, Set.of(new Position(5, 6)));
            ProbeController c = new ProbeController(gridWithObstacle,
                    new Probe(new Position(5, 5), Direction.NORTH));

            assertThatThrownBy(() -> c.execute("F"))
                    .isInstanceOf(ObstacleEncounteredException.class)
                    .hasMessageContaining("(5, 6)");
        }

        @Test
        void probePositionUnchangedAfterObstacleCollision() {
            Grid gridWithObstacle = new Grid(10, 10, Set.of(new Position(5, 6)));
            ProbeController c = new ProbeController(gridWithObstacle,
                    new Probe(new Position(5, 5), Direction.NORTH));

            assertThatThrownBy(() -> c.execute("F"));
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(5, 5));
        }

        @Test
        void probeCanNavigateAroundObstacle() {
            // Obstacle at (5,6); navigate right, forward, left to pass it
            Grid gridWithObstacle = new Grid(10, 10, Set.of(new Position(5, 6)));
            ProbeController c = new ProbeController(gridWithObstacle,
                    new Probe(new Position(5, 5), Direction.NORTH));

            c.execute("RFLF"); // East → (6,5) → North → (6,6)
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(6, 6));
        }

        @Test
        void startingOnObstacleThrowsException() {
            Grid gridWithObstacle = new Grid(10, 10, Set.of(new Position(2, 2)));
            assertThatThrownBy(() ->
                    new ProbeController(gridWithObstacle, new Probe(new Position(2, 2), Direction.NORTH)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -------------------------------------------------------------------------
    // Visit tracking
    // -------------------------------------------------------------------------

    @Nested
    class VisitTracking {

        @Test
        void startingPositionIsAlwaysRecordedAsFirstVisit() {
            ProbeController c = controller(3, 3, Direction.NORTH);
            assertThat(c.getProbe().getVisitedPositions()).contains(new Position(3, 3));
        }

        @Test
        void eachVisitedPositionIsRecorded() {
            ProbeController c = controller(0, 0, Direction.NORTH);
            c.execute("FF");
            assertThat(c.getProbe().getVisitedPositions()).containsExactly(
                    new Position(0, 0),
                    new Position(0, 1),
                    new Position(0, 2)
            );
        }

        @Test
        void revisitingPositionDoesNotDuplicate() {
            ProbeController c = controller(5, 5, Direction.NORTH);
            c.execute("FB"); // forward to (5,6) then back to (5,5)
            assertThat(c.getProbe().getVisitedPositions())
                    .containsExactlyInAnyOrder(new Position(5, 5), new Position(5, 6));
        }

        @Test
        void turningDoesNotAddNewVisitEntry() {
            ProbeController c = controller(5, 5, Direction.NORTH);
            c.execute("LLRR"); // only turns, no movement
            assertThat(c.getProbe().getVisitedPositions()).hasSize(1);
        }
    }

    // -------------------------------------------------------------------------
    // Command parsing
    // -------------------------------------------------------------------------

    @Nested
    class CommandParsing {

        @Test
        void commandsAreCaseInsensitive() {
            ProbeController c = controller(5, 5, Direction.NORTH);
            c.execute("f"); // lowercase
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(5, 6));
        }

        @Test
        void unknownCommandCharacterThrowsException() {
            ProbeController c = controller(5, 5, Direction.NORTH);
            assertThatThrownBy(() -> c.execute("X"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unknown command");
        }

        @Test
        void nullCommandStringIsIgnored() {
            ProbeController c = controller(5, 5, Direction.NORTH);
            c.execute((String) null);
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(5, 5));
        }

        @Test
        void emptyCommandStringIsIgnored() {
            ProbeController c = controller(5, 5, Direction.NORTH);
            c.execute("");
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(5, 5));
        }

        @Test
        void executeCommandListWorksSameAsString() {
            ProbeController c = controller(0, 0, Direction.NORTH);
            c.execute(List.of(Command.F, Command.F, Command.R, Command.F));
            assertThat(c.getProbe().getPosition()).isEqualTo(new Position(1, 2));
            assertThat(c.getProbe().getDirection()).isEqualTo(Direction.EAST);
        }
    }

    // -------------------------------------------------------------------------
    // Integration scenario
    // -------------------------------------------------------------------------

    @Test
    void fullNavigationScenario() {
        // 10x10 grid, obstacle at (3,4)
        // Start: (1,1) facing NORTH
        // Commands: FFRFF — two north, turn right (facing EAST), two east
        Grid g = new Grid(10, 10, Set.of(new Position(3, 4)));
        ProbeController c = new ProbeController(g, new Probe(new Position(1, 1), Direction.NORTH));
        c.execute("FFRFF");

        assertThat(c.getProbe().getPosition()).isEqualTo(new Position(3, 3));
        assertThat(c.getProbe().getDirection()).isEqualTo(Direction.EAST);
        assertThat(c.getProbe().getVisitedPositions()).containsExactly(
                new Position(1, 1),
                new Position(1, 2),
                new Position(1, 3),
                new Position(2, 3),
                new Position(3, 3)
        );
    }
}
