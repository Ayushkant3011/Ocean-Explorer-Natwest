package com.natwest.oceanexplorer.model;

/**
 * Commands that can be issued to the probe.
 *
 * F - Move forward (one step in the direction currently facing)
 * B - Move backward (one step opposite to the direction facing)
 * L - Rotate 90 degrees left (counter-clockwise), no movement
 * R - Rotate 90 degrees right (clockwise), no movement
 */
public enum Command {
    F, B, L, R;

    
    public static Command from(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'F' -> F;
            case 'B' -> B;
            case 'L' -> L;
            case 'R' -> R;
            default  -> throw new IllegalArgumentException("Unknown command: '" + c + "'. Valid commands are F, B, L, R.");
        };
    }
}
