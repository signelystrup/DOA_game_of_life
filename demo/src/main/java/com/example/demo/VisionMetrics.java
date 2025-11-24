package com.example.demo;

import lombok.Getter;

/**
 * Tracks efficiency of vision/grid searches for performance analysis.
 * Measures how many entities are fetched from grid vs actually within vision range.
 */
@Getter
public class VisionMetrics {
    private int entitiesFetched = 0;    // Total entities retrieved from grid
    private int entitiesInVision = 0;   // Entities actually within vision range

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
     * Reset all counters (call at start of each frame)
     */
    public void reset() {
        entitiesFetched = 0;
        entitiesInVision = 0;
    }

    /**
     * Add another metrics object to this one (for aggregation)
     */
    public void add(VisionMetrics other) {
        this.entitiesFetched += other.entitiesFetched;
        this.entitiesInVision += other.entitiesInVision;
    }

    @Override
    public String toString() {
        return String.format(
            "Fetched: %d | In Vision: %d | Wasted: %d (%.1f%% efficiency)",
            entitiesFetched, entitiesInVision, getWastedEntities(), getEfficiencyPercent()
        );
    }
}
