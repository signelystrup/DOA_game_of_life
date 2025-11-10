package com.example.demo;

import java.util.ArrayList;
import java.util.List;

/**
 * Spatial Hash Grid for efficient entity lookup
 *
 * Divides the world into a grid of cells. Each cell contains entities in that area.
 * This lets you find nearby entities without checking ALL entities in the world.
 *
 * IMPORTANT: findNearby() returns ALL entities from 9 cells (3x3 grid).
 * YOU must calculate actual distances to filter by vision range!
 *
 * Performance: Reduces checks from 1000 entities to ~50 entities (20x faster)
 */
public class Grid {
    private final int cellSize;
    private final int gridWidth;
    private final int gridHeight;
    private final List<Object>[][] cells;

    /**
     * Create a spatial grid
     *
     * @param worldWidth Width of game world in pixels (e.g., 500)
     * @param worldHeight Height of game world in pixels (e.g., 500)
     * @param cellSize Size of each cell in pixels (e.g., 50)
     */
    @SuppressWarnings("unchecked")
    public Grid(int worldWidth, int worldHeight, int cellSize) {
        this.cellSize = cellSize;
        this.gridWidth = worldWidth / cellSize;
        this.gridHeight = worldHeight / cellSize;

        // Create 2D array of ArrayLists
        cells = new ArrayList[gridWidth][gridHeight];

        // Initialize each cell
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                cells[x][y] = new ArrayList<>();
            }
        }
    }

    /**
     * Insert entity into grid
     *
     * Example:
     *   Bunny bunny = new Bunny(100, 150);
     *   grid.insert(bunny, bunny.x, bunny.y);
     */
    public void insert(Object entity, float worldX, float worldY) {
        int gridX = worldToGridX(worldX);
        int gridY = worldToGridY(worldY);
        cells[gridX][gridY].add(entity);
    }

    /**
     * Remove entity from grid
     *
     * Example:
     *   grid.remove(bunny, bunny.x, bunny.y);
     */
    public void remove(Object entity, float worldX, float worldY) {
        int gridX = worldToGridX(worldX);
        int gridY = worldToGridY(worldY);
        cells[gridX][gridY].remove(entity);
    }

    /**
     * Find all entities in nearby cells (3x3 grid)
     *
     * Returns ALL entities from 9 cells - does NOT check distances!
     * You must calculate distances yourself to filter by vision range.
     *
     * Example:
     *   List<Object> nearby = grid.findNearby(rabbit.x, rabbit.y);
     *   for (Object obj : nearby) {
     *       if (obj instanceof Wolf) {
     *           Wolf wolf = (Wolf)obj;
     *           float dx = wolf.x - rabbit.x;
     *           float dy = wolf.y - rabbit.y;
     *           float distance = (float)Math.sqrt(dx*dx + dy*dy);
     *           if (distance < 60) {
     *               // Wolf is within vision!
     *           }
     *       }
     *   }
     *
     * @return List of all entities from nearby cells (not filtered by distance)
     */
    public List<Object> findNearby(float worldX, float worldY) {
        List<Object> nearby = new ArrayList<>();

        int centerX = worldToGridX(worldX);
        int centerY = worldToGridY(worldY);

        // Check 3x3 grid (current cell + 8 neighbors)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int checkX = centerX + dx;
                int checkY = centerY + dy;

                // Stay within grid bounds
                if (checkX >= 0 && checkX < gridWidth &&
                    checkY >= 0 && checkY < gridHeight) {
                    nearby.addAll(cells[checkX][checkY]);
                }
            }
        }

        return nearby;
    }

    /**
     * Clear all entities from grid
     */
    public void clear() {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                cells[x][y].clear();
            }
        }
    }

    /**
     * Check if a cell is empty at given world coordinates
     * Useful for spawning grass at random empty spots
     *
     * Example:
     *   Random random = new Random();
     *   int x = random.nextInt(500);
     *   int y = random.nextInt(500);
     *   if (grid.isEmpty(x, y)) {
     *       Grass grass = new Grass(x, y);
     *       grid.insert(grass, x, y);
     *   }
     */
    public boolean isEmpty(float worldX, float worldY) {
        int gridX = worldToGridX(worldX);
        int gridY = worldToGridY(worldY);
        return cells[gridX][gridY].isEmpty();
    }

    /**
     * Calculate distance between two points
     * Static helper method - can be called without a Grid instance
     *
     * Example:
     *   float dist = Grid.calculateDistance(rabbit.x, rabbit.y, wolf.x, wolf.y);
     *   if (dist < 60) {
     *       // Wolf is within vision!
     *   }
     */
    public static float calculateDistance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Convert world X coordinate to grid X coordinate
     */
    private int worldToGridX(float worldX) {
        int gridX = (int)(worldX / cellSize);
        if (gridX < 0) gridX = 0;
        if (gridX >= gridWidth) gridX = gridWidth - 1;
        return gridX;
    }

    /**
     * Convert world Y coordinate to grid Y coordinate
     */
    private int worldToGridY(float worldY) {
        int gridY = (int)(worldY / cellSize);
        if (gridY < 0) gridY = 0;
        if (gridY >= gridHeight) gridY = gridHeight - 1;
        return gridY;
    }

    // Getters
    public int getCellSize() { return cellSize; }
    public int getGridWidth() { return gridWidth; }
    public int getGridHeight() { return gridHeight; }
}
