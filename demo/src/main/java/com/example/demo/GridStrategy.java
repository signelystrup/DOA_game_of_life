package com.example.demo;

import com.example.demo.entities.Wolf;

/**
 * Different grid search strategies for performance testing and comparison.
 * Change GameConfig.STRATEGY to switch between approaches.
 */
public enum GridStrategy {
    /**
     * Simple approach: Grid size based on max vision (wolf vision = 80)
     * All entities search 3×3 grid
     * Pro: Simple, guaranteed to work
     * Con: May fetch unnecessary entities for bunnies
     */
    SAFE_MAX_VISION {
        @Override
        public int getCellSize() {
            return Math.max(RABBIT_VISION, WOLF_VISION); // 80
        }

        @Override
        public int getSearchRadius(Class<?> entityType) {
            return 1; // Everyone searches 3×3 (1 cell away)
        }

        @Override
        public String getDescription() {
            return "Safe: cell=80px, all entities 3×3 grid";
        }
    },

    /**
     * Optimized for rabbits (most common entity)
     * Grid size = 40 (rabbit vision)
     * Rabbits: 3×3 (1 cell), Wolves: 5×5 (2 cells)
     * Pro: Rabbits fetch fewer entities
     * Con: Wolves do more grid checks
     */
    OPTIMIZED_FOR_COMMON {
        @Override
        public int getCellSize() {
            return RABBIT_VISION; // 40
        }

        @Override
        public int getSearchRadius(Class<?> entityType) {
            if (Wolf.class.isAssignableFrom(entityType)) {
                return 2; // Wolves: 5×5 grid (2 cells away)
            }
            return 1; // Bunnies: 3×3 grid (1 cell away)
        }

        @Override
        public String getDescription() {
            return "Optimized: cell=40px, bunnies 3×3, wolves 5×5";
        }
    },

    /**
     * Very small cells for high entity density
     * Grid size = 20 (half of rabbit vision)
     * More grid checks but fewer entities per cell
     * Pro: Very small cells = minimal wasted entities
     * Con: More cell boundary checks
     */
    SMALL_CELLS {
        @Override
        public int getCellSize() {
            return 20; // Half of rabbit vision
        }

        @Override
        public int getSearchRadius(Class<?> entityType) {
            if (Wolf.class.isAssignableFrom(entityType)) {
                return 4; // Wolves: 9×9 grid (80/20 = 4 cells)
            }
            return 2; // Bunnies: 5×5 grid (40/20 = 2 cells)
        }

        @Override
        public String getDescription() {
            return "Small cells: cell=20px, bunnies 5×5, wolves 9×9";
        }
    };

    public static final int RABBIT_VISION = 40;
    public static final int WOLF_VISION = 80;

    public abstract int getCellSize();
    public abstract int getSearchRadius(Class<?> entityType);
    public abstract String getDescription();
}
