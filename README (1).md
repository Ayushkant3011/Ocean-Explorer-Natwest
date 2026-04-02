# Ocean Explorer

A coding kata built for the NatWest Software Engineer assessment.

The brief was to build an API that controls a remotely operated submersible probe moving across a grid representing the ocean floor.

---

## How it works

You define a grid (width x height), optionally place obstacles on it, drop the probe at a starting position with a facing direction, then send it a string of commands:

| Command | Action |
|---|---|
| `F` | Move forward one step |
| `B` | Move backward one step |
| `L` | Rotate 90° left (no movement) |
| `R` | Rotate 90° right (no movement) |

The probe won't move if a command would take it off the grid or into an obstacle — an exception is thrown instead and the probe stays put.

---

## Running it

You need Java 17+ and Maven installed.

**Run the tests:**
```bash
mvn test
```

**Run the demo:**
```bash
mvn compile exec:java -Dexec.mainClass="com.natwest.oceanexplorer.Main"
```

**Run interactively** (enter your own grid, obstacles and commands via the terminal):
```bash
mvn compile exec:java -Dexec.mainClass="com.natwest.oceanexplorer.Main" -Dexec.args="--interactive"
```

---

## Project structure

```
src/
├── main/java/com/natwest/oceanexplorer/
│   ├── Main.java                        entry point, demo + interactive mode
│   ├── model/
│   │   ├── Direction.java               N/S/E/W enum, handles rotation and movement deltas
│   │   ├── Command.java                 F/B/L/R enum with char parser
│   │   ├── Position.java                immutable x/y value object
│   │   ├── Grid.java                    grid dimensions + obstacle positions
│   │   └── Probe.java                   probe state — position, direction, visited history
│   ├── service/
│   │   ├── ProbeController.java         core API — processes commands, enforces constraints
│   │   ├── GridVisualiser.java          ASCII grid renderer
│   │   └── CommandParser.java           interactive CLI (reads from any Scanner)
│   └── exception/
│       ├── BoundaryExceededException.java
│       ├── ObstacleEncounteredException.java
│       └── InvalidGridException.java
└── test/java/com/natwest/oceanexplorer/
    ├── model/
    │   ├── DirectionTest.java
    │   └── GridTest.java
    └── service/
        ├── ProbeControllerTest.java
        ├── GridVisualiserTest.java
        └── CommandParserTest.java
```

---

## Design notes

**Why typed exceptions instead of returning false?**
I wanted the API to be honest about what went wrong. `BoundaryExceededException` and `ObstacleEncounteredException` carry the blocked position so the caller has context to decide what to do — re-route, log, stop. Returning a boolean would lose that information.

**Why LinkedHashSet for visited positions?**
Needed two things at once — no duplicates when the probe revisits a cell, and insertion order preserved for the summary printout. LinkedHashSet gives both without any extra work.

**Why is Direction an enum with methods?**
Keeping rotation logic (`turnLeft`, `turnRight`) and movement deltas (`deltaX`, `deltaY`) on the enum itself means ProbeController doesn't need a big switch statement. Each direction knows how it behaves — that felt cleaner.

---

## What the visualiser looks like

```
7 | ~ ~ ~ ~ ~ ~ ~ ~
6 | ~ ~ ~ ~ ~ ~ ~ ~
5 | ~ ~ ~ ~ ~ > ~ ~
4 | ~ ~ ~ # # ~ ~ ~
3 | ~ ~ . . . ~ ~ ~
2 | ~ ~ . ~ ~ ~ ~ ~
1 | ~ ~ . ~ ~ ~ ~ ~
0 | ~ ~ ~ ~ ~ ~ ~ ~
    -----------------
    0 1 2 3 4 5 6 7  X
```

`^` `>` `v` `<` = probe facing N/E/S/W &nbsp;&nbsp; `#` = obstacle &nbsp;&nbsp; `.` = visited &nbsp;&nbsp; `~` = open water
