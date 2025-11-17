package com.example.demo;

import com.example.demo.entities.Grass;

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

    private final int PROXIMITY_INTERVAL = 120;  // 2.0 seconds at 60 FPS
    private final int RANDOM_INTERVAL = 240;     // 4.0 seconds at 60 FPS
    private final int SPREAD_RADIUS = 25;        // Pixels

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

        if (proximityTimer >= PROXIMITY_INTERVAL) {
            proximityTimer = 0;
            spawnNearExistingGrass();
        }

        if (randomTimer >= RANDOM_INTERVAL) {
            randomTimer = 0;
            spawnAtRandomLocation();
        }
    }

    /**
     * Fast spawning: Find existing grass and spawn nearby
     * Creates natural spreading and clustering
     */
    private void spawnNearExistingGrass() {
        int attempts = 10;

        for (int i = 0; i < attempts; i++) {
            int x = random.nextInt(500);
            int y = random.nextInt(500);

            List<Object> nearby = grid.findNearby(x, y);
            Grass foundGrass = null;

            for (Object obj : nearby) {
                if (obj instanceof Grass) {
                    foundGrass = (Grass) obj;
                    break;
                }
            }

            if (foundGrass != null) {
                spawnNearGrass(foundGrass);
                return;
            }
        }
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

            int newX = existingGrass.worldX + offsetX;
            int newY = existingGrass.worldY + offsetY;

            newX = Math.max(0, Math.min(newX, 499));
            newY = Math.max(0, Math.min(newY, 499));

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
        int maxAttempts = 10;

        for (int i = 0; i < maxAttempts; i++) {
            int x = random.nextInt(500);
            int y = random.nextInt(500);

            if (grid.isEmpty(x, y)) {
                Grass grass = new Grass(x, y);
                grid.insert(grass, x, y);
                //System.out.println("New grass patch spawned at (" + x + ", " + y + ")");
                return;
            }
        }
    }
}
