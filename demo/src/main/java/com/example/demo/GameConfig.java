package com.example.demo;

/**
 * Game configuration constants with strategy-based grid optimization.
 *
 * TO SWITCH STRATEGIES: Change the STRATEGY constant below
 */
public class GameConfig {
    // *** CHANGE THIS LINE TO SWITCH GRID STRATEGIES ***
    // we have 3 strategies: "OPTIMIZED_FOR_WOLVES" "OPTIMIZED_FOR_BUNNIES" "OPTIMIZED_FOR_EFFICIENCY"
    // Note: Non-final to allow runtime strategy changes (used by BenchmarkRunner)
    public static GridStrategy STRATEGY = GridStrategy.OPTIMIZED_FOR_WOLVES;

    // Vision ranges for each species (in pixels)
    public static final int RABBIT_VISION = 40;
    public static final int WOLF_VISION = 80;

    /**
     * Grid cell size determined by selected strategy
     * Different strategies optimize for different scenarios
     * Calculated dynamically to support runtime strategy changes
     */
    public static int getGridCellSize() {
        return STRATEGY.getCellSize();
    }

    // World dimensions
    public static final int WORLD_WIDTH = 1300;
    public static final int WORLD_HEIGHT = 800;

    /**
     * Get the grid search radius for a specific entity type
     * This determines how many cells away to search (1 = 3×3, 2 = 5×5, etc.)
     */
    public static int getSearchRadius(Class<?> entityType) {
        return STRATEGY.getSearchRadius(entityType);
    }

    /**
     * Get description of current strategy
     */
    public static String getStrategyDescription() {
        return STRATEGY.getDescription();
    }
}
