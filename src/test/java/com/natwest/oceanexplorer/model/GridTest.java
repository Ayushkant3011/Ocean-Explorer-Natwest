package com.natwest.oceanexplorer.model;

import com.natwest.oceanexplorer.exception.InvalidGridException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GridTest {

    @Test
    void positionInsideBoundsIsWithinBounds() {
        Grid grid = new Grid(5, 5);
        assertThat(grid.isWithinBounds(new Position(2, 2))).isTrue();
    }

    @Test
    void positionAtOriginIsWithinBounds() {
        Grid grid = new Grid(5, 5);
        assertThat(grid.isWithinBounds(new Position(0, 0))).isTrue();
    }

    @Test
    void positionAtTopRightCornerIsWithinBounds() {
        Grid grid = new Grid(5, 5);
        assertThat(grid.isWithinBounds(new Position(4, 4))).isTrue();
    }

    @Test
    void negativeXIsOutOfBounds() {
        Grid grid = new Grid(5, 5);
        assertThat(grid.isWithinBounds(new Position(-1, 2))).isFalse();
    }

    @Test
    void negativeYIsOutOfBounds() {
        Grid grid = new Grid(5, 5);
        assertThat(grid.isWithinBounds(new Position(2, -1))).isFalse();
    }

    @Test
    void xEqualToWidthIsOutOfBounds() {
        Grid grid = new Grid(5, 5);
        assertThat(grid.isWithinBounds(new Position(5, 2))).isFalse();
    }

    @Test
    void yEqualToHeightIsOutOfBounds() {
        Grid grid = new Grid(5, 5);
        assertThat(grid.isWithinBounds(new Position(2, 5))).isFalse();
    }

    @Test
    void obstaclePositionIsDetected() {
        Position obstacle = new Position(3, 3);
        Grid grid = new Grid(5, 5, Set.of(obstacle));
        assertThat(grid.hasObstacleAt(obstacle)).isTrue();
    }

    @Test
    void nonObstaclePositionReturnsNoObstacle() {
        Grid grid = new Grid(5, 5, Set.of(new Position(3, 3)));
        assertThat(grid.hasObstacleAt(new Position(1, 1))).isFalse();
    }

    @Test
    void positionWithObstacleIsNotNavigable() {
        Position obstacle = new Position(2, 2);
        Grid grid = new Grid(5, 5, Set.of(obstacle));
        assertThat(grid.isNavigable(obstacle)).isFalse();
    }

    @Test
    void outOfBoundsPositionIsNotNavigable() {
        Grid grid = new Grid(5, 5);
        assertThat(grid.isNavigable(new Position(10, 10))).isFalse();
    }

    @Test
    void freeInBoundsPositionIsNavigable() {
        Grid grid = new Grid(5, 5);
        assertThat(grid.isNavigable(new Position(2, 2))).isTrue();
    }

    @Test
    void gridWithZeroWidthThrowsException() {
        assertThatThrownBy(() -> new Grid(0, 5))
                .isInstanceOf(InvalidGridException.class)
                .hasMessageContaining("at least 1x1");
    }

    @Test
    void gridWithZeroHeightThrowsException() {
        assertThatThrownBy(() -> new Grid(5, 0))
                .isInstanceOf(InvalidGridException.class);
    }

    @Test
    void obstacleOutsideGridThrowsException() {
        assertThatThrownBy(() -> new Grid(5, 5, Set.of(new Position(10, 10))))
                .isInstanceOf(InvalidGridException.class)
                .hasMessageContaining("outside the grid bounds");
    }

    @Test
    void oneByOneGridIsValid() {
        Grid grid = new Grid(1, 1);
        assertThat(grid.isWithinBounds(new Position(0, 0))).isTrue();
    }
}
