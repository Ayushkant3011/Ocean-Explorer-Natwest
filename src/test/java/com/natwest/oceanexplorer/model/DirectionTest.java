package com.natwest.oceanexplorer.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DirectionTest {

    @Test
    void turningRightFromNorthFacesEast() {
        assertThat(Direction.NORTH.turnRight()).isEqualTo(Direction.EAST);
    }

    @Test
    void turningRightFromEastFacesSouth() {
        assertThat(Direction.EAST.turnRight()).isEqualTo(Direction.SOUTH);
    }

    @Test
    void turningRightFromSouthFacesWest() {
        assertThat(Direction.SOUTH.turnRight()).isEqualTo(Direction.WEST);
    }

    @Test
    void turningRightFromWestFacesNorth() {
        assertThat(Direction.WEST.turnRight()).isEqualTo(Direction.NORTH);
    }

    @Test
    void turningLeftFromNorthFacesWest() {
        assertThat(Direction.NORTH.turnLeft()).isEqualTo(Direction.WEST);
    }

    @Test
    void turningLeftFromWestFacesSouth() {
        assertThat(Direction.WEST.turnLeft()).isEqualTo(Direction.SOUTH);
    }

    @Test
    void turningLeftFromSouthFacesEast() {
        assertThat(Direction.SOUTH.turnLeft()).isEqualTo(Direction.EAST);
    }

    @Test
    void turningLeftFromEastFacesNorth() {
        assertThat(Direction.EAST.turnLeft()).isEqualTo(Direction.NORTH);
    }

    @Test
    void fourRightTurnsReturnToOriginalDirection() {
        Direction dir = Direction.NORTH;
        for (int i = 0; i < 4; i++) dir = dir.turnRight();
        assertThat(dir).isEqualTo(Direction.NORTH);
    }

    @Test
    void fourLeftTurnsReturnToOriginalDirection() {
        Direction dir = Direction.SOUTH;
        for (int i = 0; i < 4; i++) dir = dir.turnLeft();
        assertThat(dir).isEqualTo(Direction.SOUTH);
    }

    @Test
    void northMovesPositiveY() {
        assertThat(Direction.NORTH.deltaX()).isEqualTo(0);
        assertThat(Direction.NORTH.deltaY()).isEqualTo(1);
    }

    @Test
    void southMovesNegativeY() {
        assertThat(Direction.SOUTH.deltaX()).isEqualTo(0);
        assertThat(Direction.SOUTH.deltaY()).isEqualTo(-1);
    }

    @Test
    void eastMovesPositiveX() {
        assertThat(Direction.EAST.deltaX()).isEqualTo(1);
        assertThat(Direction.EAST.deltaY()).isEqualTo(0);
    }

    @Test
    void westMovesNegativeX() {
        assertThat(Direction.WEST.deltaX()).isEqualTo(-1);
        assertThat(Direction.WEST.deltaY()).isEqualTo(0);
    }
}
