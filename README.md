# Ocean Explorer — NatWest Coding Kata

## Overview

A Java 17 API for controlling a remotely operated submersible probe navigating a defined ocean floor grid.
The solution is test-driven, fully object-oriented, and ships with an interactive CLI and an ASCII grid visualiser.

---

## Running the Project

### Run all tests
```bash
mvn test
```

### Run the scripted demo
```bash
mvn compile exec:java -Dexec.mainClass="com.natwest.oceanexplorer.Main"
```

### Run interactively (stdin)
```bash
mvn compile exec:java -Dexec.mainClass="com.natwest.oceanexplorer.Main" -Dexec.args="--interactive"
```

You will be prompted to enter:
1. Grid dimensions (e.g. `10 10`)
2. Number of obstacles, then each obstacle (e.g. `3 4`)
3. Probe start + direction (e.g. `1 1 NORTH`)
4. Command sequences (e.g. `FFRFF`) — type `summary` or `quit` to finish

---

## Design Decisions

### Domain Model

| Class | Responsibility |
|---|---|
| `Direction` | Enum with `turnLeft`, `turnRight`, `deltaX`, `deltaY` built in |
| `Command` | Enum for `F/B/L/R` with a `from(char)` factory (case-insensitive) |
| `Position` | Immutable value object with `equals`/`hashCode` |
| `Grid` | Dimensions + obstacles; validates all constraints at construction time |
| `Probe` | Mutable state: position, direction, `LinkedHashSet` of visited positions |
| `ProbeController` | Core API — orchestrates commands and enforces grid rules |
| `GridVisualiser` | Renders the grid as ASCII art with directional probe glyph |
| `CommandParser` | Interactive CLI — reads setup and commands from any `Scanner` |

### Error Handling

| Exception | When thrown |
|---|---|
| `BoundaryExceededException` | Move would leave the grid |
| `ObstacleEncounteredException` | Move would enter an obstacle cell |
| `InvalidGridException` | Invalid dimensions or out-of-bounds obstacle |

The probe stays in place on any blocked move.

### ASCII Visualiser Legend

| Char | Meaning |
|---|---|
| `^` `>` `v` `<` | Probe facing N / E / S / W |
| `#` | Obstacle |
| `.` | Visited, currently unoccupied |
| `~` | Unvisited open water |

---

## Project Structure

```
src/
├── main/java/com/natwest/oceanexplorer/
│   ├── Main.java
│   ├── model/         Direction, Command, Position, Grid, Probe
│   ├── service/       ProbeController, GridVisualiser, CommandParser
│   └── exception/     BoundaryExceededException, ObstacleEncounteredException, InvalidGridException
└── test/java/com/natwest/oceanexplorer/
    ├── model/         DirectionTest, GridTest
    └── service/       ProbeControllerTest, GridVisualiserTest, CommandParserTest
```

## Test Coverage Summary

| Test class | What it covers |
|---|---|
| `DirectionTest` | All rotations, full 360° cycles, all delta values |
| `GridTest` | Bounds checking, obstacle detection, invalid construction |
| `ProbeControllerTest` | All movement types, boundaries, obstacles, visit tracking, parsing, integration |
| `GridVisualiserTest` | All probe glyphs, obstacles, visited trail, open water, axis labels |
| `CommandParserTest` | Move confirmation, obstacle/boundary warnings, summary, quit, blank lines |
