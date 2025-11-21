package com.example.demo;

/**
 * Tracks efficiency of vision/grid searches for performance analysis.
 * Measures how many entities are fetched from grid vs actually within vision range.
 */
public class VisionMetrics {
    private int entitiesFetched = 0;    // Total entities retrieved from grid
    private int entitiesInVision = 0;   // Entities actually within vision range
    private int visionChecks = 0;       // Number of distance calculations performed

    /**
     * Record entities fetched from grid
     */
    public void recordFetch(int count) {
        entitiesFetched += count;
    }

    /**
     * Record entities that were actually in vision range
     */
    public void recordInVision(int count) {
        entitiesInVision += count;
    }

    /**
     * Record a distance check calculation
     */
    public void recordCheck() {
        visionChecks++;
    }

    /**
     * Calculate how many entities were fetched but not used
     */
    public int getWastedEntities() {
        return entitiesFetched - entitiesInVision;
    }

    /**
     * Calculate efficiency as percentage of fetched entities that were in vision
     */
    public double getEfficiencyPercent() {
        if (entitiesFetched == 0) return 100.0;
        return 100.0 * entitiesInVision / entitiesFetched;
    }

    /**
     * Get total entities fetched
     */
    public int getEntitiesFetched() {
        return entitiesFetched;
    }

    /**
     * Get entities in vision
     */
    public int getEntitiesInVision() {
        return entitiesInVision;
    }

    /**
     * Get number of vision checks
     */
    public int getVisionChecks() {
        return visionChecks;
    }

    /**
     * Reset all counters (call at start of each frame)
     */
    public void reset() {
        entitiesFetched = 0;
        entitiesInVision = 0;
        visionChecks = 0;
    }

    /**
     * Add another metrics object to this one (for aggregation)
     */
    public void add(VisionMetrics other) {
        this.entitiesFetched += other.entitiesFetched;
        this.entitiesInVision += other.entitiesInVision;
        this.visionChecks += other.visionChecks;
    }

    @Override
    public String toString() {
        return String.format(
            "Fetched: %d | In Vision: %d | Wasted: %d (%.1f%% efficiency)",
            entitiesFetched, entitiesInVision, getWastedEntities(), getEfficiencyPercent()
        );
    }
}
