package com.example.demo;

import com.example.demo.entities.Wolf;

/**
 * Different grid search strategies for performance testing and comparison.
 * Change GameConfig.STRATEGY to switch between approaches.
 */
public enum GridStrategy {
    /**
     * Optimized for wolves: Grid size based on wolf vision (80px)
     * All entities search 3×3 grid
     * Pro: Simple, guaranteed to work for wolves
     * Con: May fetch unnecessary entities for bunnies
     */
    OPTIMIZED_FOR_WOLVES {
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
            return "Optimized for wolves: cell=80px, all entities 3×3 grid";
        }
    },

    /**
     * Optimized for bunnies (most common entity)
     * Grid size = 40 (bunny vision)
     * Bunnies: 3×3 (1 cell), Wolves: 5×5 (2 cells)
     * Pro: Bunnies fetch fewer entities
     * Con: Wolves do more grid checks
     */
    OPTIMIZED_FOR_BUNNIES {
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
            return "Optimized for bunnies: cell=40px, bunnies 3×3, wolves 5×5";
        }
    },

    /**
     * Optimized for efficiency: Very small cells for high entity density
     * Grid size = 20 (half of bunny vision)
     * More grid checks but fewer entities per cell
     * Pro: Very small cells = minimal wasted entities
     * Con: More cell boundary checks
     */
    OPTIMIZED_FOR_EFFICIENCY {
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
            return "Optimized for efficiency: cell=20px, bunnies 5×5, wolves 9×9";
        }
    };

    public static final int RABBIT_VISION = 40;
    public static final int WOLF_VISION = 80;

    public abstract int getCellSize();
    public abstract int getSearchRadius(Class<?> entityType);
    public abstract String getDescription();
}
