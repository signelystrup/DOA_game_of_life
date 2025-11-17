package com.example.demo;

/**
 * Game configuration constants
 * Defines vision ranges and calculates optimal grid cell size
 */
public class GameConfig {
    // Vision ranges for each species (in pixels)
    public static final int RABBIT_VISION = 40;
    public static final int WOLF_VISION = 60;

    /**
     * Grid cell size must be >= maximum vision range
     * This ensures findNearby() returns all entities within any animal's vision
     *
     * Why max? If cell size < vision range, animals might miss neighbors
     * in adjacent cells that are still within their vision distance.
     */
    public static final int GRID_CELL_SIZE = Math.max(RABBIT_VISION, WOLF_VISION);

    // World dimensions
    public static final int WORLD_WIDTH = 1200;
    public static final int WORLD_HEIGHT = 800;
}
