package com.example.demo;

import java.util.ArrayList;
import java.util.List;

/**
 * Spatial Hash Grid for efficient entity lookup.
 * Divides the world into cells for fast neighbor queries.
 */
public class Grid {
    private final int cellSize;
    private final int gridWidth;
    private final int gridHeight;
    private final List<Object>[][] cells;

    /**
     * Create a spatial grid
     *
     * @param worldWidth Width of game world in pixels
     * @param worldHeight Height of game world in pixels
     * @param cellSize Size of each cell in pixels
     */
    @SuppressWarnings("unchecked")
    public Grid(int worldWidth, int worldHeight, int cellSize) {
        this.cellSize = cellSize;
        this.gridWidth = worldWidth / cellSize;
        this.gridHeight = worldHeight / cellSize;

        cells = new ArrayList[gridWidth][gridHeight];

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                cells[x][y] = new ArrayList<>();
            }
        }
    }

    /**
     * Insert entity into grid at given position
     */
    public void insert(Object entity, float worldX, float worldY) {
        int gridX = worldToGridX(worldX);
        int gridY = worldToGridY(worldY);
        cells[gridX][gridY].add(entity);
    }

    /**
     * Remove entity from grid at given position
     */
    public void remove(Object entity, float worldX, float worldY) {
        int gridX = worldToGridX(worldX);
        int gridY = worldToGridY(worldY);
        cells[gridX][gridY].remove(entity);
    }

    /**
     * Find all entities in nearby cells with configurable radius
     * Note: Returns all entities from cells, not filtered by distance
     *
     * @param worldX X position in world coordinates
     * @param worldY Y position in world coordinates
     * @param cellRadius How many cells away to search (1 = 3×3, 2 = 5×5, etc.)
     * @return List of entities from nearby cells
     */
    public List<Object> findNearby(float worldX, float worldY, int cellRadius) {
        List<Object> nearby = new ArrayList<>();

        int centerX = worldToGridX(worldX);
        int centerY = worldToGridY(worldY);

        // Search from -cellRadius to +cellRadius in both dimensions
        for (int dx = -cellRadius; dx <= cellRadius; dx++) {
            for (int dy = -cellRadius; dy <= cellRadius; dy++) {
                int checkX = centerX + dx;
                int checkY = centerY + dy;

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
     * Get all entities from the grid
     * Useful for rendering or iterating over all entities
     *
     * @return List containing all entities from all cells
     */
    public List<Object> getAllEntities() {
        List<Object> allEntities = new ArrayList<>();

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                allEntities.addAll(cells[x][y]);
            }
        }

        return allEntities;
    }

    /**
     * Check if a cell is empty at given position
     */
    public boolean isEmpty(float worldX, float worldY) {
        int gridX = worldToGridX(worldX);
        int gridY = worldToGridY(worldY);
        return cells[gridX][gridY].isEmpty();
    }

    /**
     * Calculate Euclidean distance between two points
     */
    public static float calculateDistance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    private int worldToGridX(float worldX) {
        int gridX = (int)(worldX / cellSize);
        if (gridX < 0) gridX = 0;
        if (gridX >= gridWidth) gridX = gridWidth - 1;
        return gridX;
    }

    private int worldToGridY(float worldY) {
        int gridY = (int)(worldY / cellSize);
        if (gridY < 0) gridY = 0;
        if (gridY >= gridHeight) gridY = gridHeight - 1;
        return gridY;
    }

    public int getCellSize() { return cellSize; }
    public int getGridWidth() { return gridWidth; }
    public int getGridHeight() { return gridHeight; }
}
