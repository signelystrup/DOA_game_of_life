package com.example.demo;

import com.example.demo.entities.Grass;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages grass spawning with two strategies:
 * 1. Fast proximity spawning - grass spreads near existing patches
 * 2. Slow random spawning - new patches colonize empty areas
 */
public class GrassManager {
    private final Grid grid;
    private final Random random;

    private int proximityTimer = 0;
    private int randomTimer = 0;

    private final int PROXIMITY_INTERVAL = 60;   // 1.0 second at 60 FPS (faster)
    private final int RANDOM_INTERVAL = 120;     // 2.0 seconds at 60 FPS (faster)
    private final int SPREAD_RADIUS = 25;        // Pixels
    private final int MAX_GRASS_COUNT = 100;     // Maximum grass patches allowed
    private final int LOW_GRASS_THRESHOLD = 50;  // Boost growth when below this

    public GrassManager(Grid grid) {
        this.grid = grid;
        this.random = new Random();
    }

    /**
     * Update grass spawning timers
     * Call this every frame
     */
    public void update() {
        proximityTimer++;
        randomTimer++;

        int grassCount = getGrassCount();
        boolean lowGrass = grassCount < LOW_GRASS_THRESHOLD;

        // Set intervals based on grass count
        int proximityThreshold = PROXIMITY_INTERVAL;
        int randomThreshold = RANDOM_INTERVAL;

        if (lowGrass) {
            proximityThreshold = PROXIMITY_INTERVAL / 2;  // 2x faster when low
            randomThreshold = RANDOM_INTERVAL / 2;        // 2x faster when low
        }

        if (proximityTimer >= proximityThreshold) {
            proximityTimer = 0;
            if (grassCount < MAX_GRASS_COUNT) {
                spawnNearExistingGrass();
            }
        }

        if (randomTimer >= randomThreshold) {
            randomTimer = 0;
            if (grassCount < MAX_GRASS_COUNT) {
                spawnAtRandomLocation();
            }
        }
    }

    /**
     * Fast spawning: Find existing grass and spawn nearby
     * Creates natural spreading and clustering
     */
    private void spawnNearExistingGrass() {
        List<Object> allEntities = grid.getAllEntities();
        List<Grass> grassList = new ArrayList<>();

        for (Object obj : allEntities) {
            if (obj instanceof Grass) {
                grassList.add((Grass) obj);
            }
        }

        if (grassList.isEmpty()) {
            return;
        }

        Grass randomGrass = grassList.get(random.nextInt(grassList.size()));
        spawnNearGrass(randomGrass);
    }

    /**
     * Spawn grass near a specific grass patch
     * Uses tight spread radius for natural clustering
     */
    private void spawnNearGrass(Grass existingGrass) {
        int maxAttempts = 5;

        for (int i = 0; i < maxAttempts; i++) {
            int offsetX = random.nextInt(SPREAD_RADIUS * 2) - SPREAD_RADIUS;
            int offsetY = random.nextInt(SPREAD_RADIUS * 2) - SPREAD_RADIUS;

            int newX = existingGrass.getWorldX() + offsetX;
            int newY = existingGrass.getWorldY() + offsetY;

            newX = Math.max(0, Math.min(newX, GameConfig.WORLD_WIDTH - 1));
            newY = Math.max(0, Math.min(newY, GameConfig.WORLD_HEIGHT - 1));

            if (grid.isEmpty(newX, newY)) {
                Grass newGrass = new Grass(newX, newY);
                grid.insert(newGrass, newX, newY);
                //System.out.println("Grass spread near existing at (" + newX + ", " + newY + ")");
                return;
            }
        }
    }

    /**
     * Slow spawning: Spawn at completely random location
     * Creates new patches for colonization
     */
    private void spawnAtRandomLocation() {
        int x = random.nextInt(GameConfig.WORLD_WIDTH);
        int y = random.nextInt(GameConfig.WORLD_HEIGHT);
        int maxAttempts = 30;

        for (int i = 0; i < maxAttempts; i++) {
            if (grid.isEmpty(x, y)) {
                Grass grass = new Grass(x, y);
                grid.insert(grass, x, y);
                //System.out.println("New grass patch spawned at (" + x + ", " + y + ")");
                return;
            }

            // Move to nearby position - randomize direction
            int offset = random.nextBoolean() ? 25 : -25;

            if (i % 2 == 0) {
                // Move x if within bounds
                if (x + offset >= 0 && x + offset < GameConfig.WORLD_WIDTH) {
                    x += offset;
                }
            } else {
                // Move y if within bounds
                if (y + offset >= 0 && y + offset < GameConfig.WORLD_HEIGHT) {
                    y += offset;
                }
            }
        }
    }

    /**
     * Count total grass entities in the grid
     */
    private int getGrassCount() {
        List<Object> allEntities = grid.getAllEntities();
        int count = 0;

        for (Object obj : allEntities) {
            if (obj instanceof Grass) {
                count++;
            }
        }

        return count;
    }
}
