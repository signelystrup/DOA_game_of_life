package com.example.demo;

/**
 * Game configuration constants with strategy-based grid optimization.
 *
 * TO SWITCH STRATEGIES: Change the STRATEGY constant below
 */
public class GameConfig {
    // *** CHANGE THIS LINE TO SWITCH GRID STRATEGIES ***
    // we have 3 strategies, "SAFE_MAX_VISION" "OPTIMIZED_FOR_COMMON" "SMALL_CELLS"
    public static final GridStrategy STRATEGY = GridStrategy.OPTIMIZED_FOR_COMMON;

    // Vision ranges for each species (in pixels)
    public static final int RABBIT_VISION = 40;
    public static final int WOLF_VISION = 80;

    /**
     * Grid cell size determined by selected strategy
     * Different strategies optimize for different scenarios
     */
    public static final int GRID_CELL_SIZE = STRATEGY.getCellSize();

    // World dimensions
    public static final int WORLD_WIDTH = 1200;
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
