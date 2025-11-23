package com.example.demo.entities;

import com.example.demo.GameConfig;
import com.example.demo.Grid;
import com.example.demo.VisionMetrics;
import lombok.Getter;
import lombok.Setter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

@Getter
@Setter
public abstract class Animal {
    public int worldX, worldY;

    // Add flocking stuff
    protected Vector2d currMovement;
    protected double speed;
    protected double visionRadius;
    protected double maxForce = 0.3; //how fast an animal can turn

    protected int destX, destY;

    // Performance metrics tracking
    public VisionMetrics metrics = new VisionMetrics();

    public Animal(int worldX, int worldY, double speed, double visionRadius){
        this.worldX = worldX;
        this.worldY = worldY;
        this.speed = speed;
        this.visionRadius = visionRadius;

        // Initialize with random velocity
        this.currMovement = new Vector2d(Math.random() - 0.5, Math.random() - 0.5); //x and y must be between -0.5 and 0.5
        this.currMovement.setMagnitude(speed);
    }

    public abstract void draw(Graphics2D g2);

    public void update(Grid grid){
        // Calculate flocking forces
        Vector2d steering = calculateSteeringForce(grid);

        // Apply steering
        currMovement.add(steering);
        currMovement.setMagnitude(speed);  // Keep speed constant

        // Update position
        worldX += (int) currMovement.x;
        worldY += (int) currMovement.y;

        wrapAroundField();
    }

    private void wrapAroundField(){
        // Wrap around edges
        if (worldX < 0) worldX = GameConfig.WORLD_WIDTH;
        if (worldX > GameConfig.WORLD_WIDTH) worldX = 0;
        if (worldY < 0) worldY = GameConfig.WORLD_HEIGHT;
        if (worldY > GameConfig.WORLD_HEIGHT) worldY = 0;
    }

    protected abstract Vector2d calculateSteeringForce(Grid grid);

    // Helper: Get animals in vision from grid (with metrics tracking)
    protected List<Animal> getAnimalsInVision(Grid grid) {
        // Get search radius for this animal type
        int searchRadius = GameConfig.getSearchRadius(this.getClass());

        List<Object> nearby = grid.findNearby(worldX, worldY, searchRadius);
        List<Animal> animals = new ArrayList<>();

        // Track how many animals we fetched vs how many are in vision
        int fetchedCount = 0;
        int inVisionCount = 0;

        for (Object obj : nearby) {
            if (obj instanceof Animal) {
                fetchedCount++;
                Animal other = (Animal) obj;
                if (other != this) {
                    double dist = Math.sqrt((worldX - other.worldX) * (worldX - other.worldX) +
                                          (worldY - other.worldY) * (worldY - other.worldY));

                    metrics.recordCheck(); // Track distance calculation

                    if (dist <= visionRadius) {
                        animals.add(other);
                        inVisionCount++;
                    }
                }
            }
        }

        // Record metrics for this search
        metrics.recordFetch(fetchedCount);
        metrics.recordInVision(inVisionCount);

        return animals;
    }

    // Helper: Get grass in vision from grid
    protected List<Grass> getGrassInVision(Grid grid) {
        // Use the same search radius as for animals
        int searchRadius = GameConfig.getSearchRadius(this.getClass());
        List<Object> nearby = grid.findNearby(worldX, worldY, searchRadius);
        List<Grass> grassList = new ArrayList<>();

        for (Object obj : nearby) {
            if (obj instanceof Grass) {
                Grass grass = (Grass) obj;
                double dist = Math.sqrt((worldX - grass.getWorldX()) * (worldX - grass.getWorldX()) +
                                      (worldY - grass.getWorldY()) * (worldY - grass.getWorldY()));
                if (dist <= visionRadius) {
                    grassList.add(grass);
                }
            }
        }

        return grassList;
    }

    // Helper: Filter animals by type
    protected <T extends Animal> List<T> filterByType(List<Animal> animals, Class<T> type) {
        List<T> filtered = new ArrayList<>();
        for (Animal animal : animals) {
            if (type.isInstance(animal)) {
                filtered.add(type.cast(animal));
            }
        }
        return filtered;
    }

    // Abstract methods for eating and breeding behavior
    // Each subclass defines what it can eat and how it breeds

    /**
     * Check if this animal can eat the given entity
     * @param entity The potential food
     * @return true if this animal can eat this entity
     */
    public abstract boolean canEat(Object entity);

    /**
     * Eat the given entity and mark this animal as fed
     * @param entity The food to eat
     */
    public abstract void eat(Object entity);

    /**
     * Check if this animal has eaten
     * @return true if the animal has eaten
     */
    public abstract boolean hasEaten();

    /**
     * Check if this animal can breed with another animal
     * @param other The potential mate
     * @return true if breeding is possible
     */
    public abstract boolean canBreedWith(Animal other);

    /**
     * Reset the animal's state after breeding
     */
    public abstract void resetAfterBreeding();

    /**
     * Get the distance threshold for eating (how close the animal needs to be)
     * @return distance in pixels
     */
    public abstract double getEatingRange();

    /**
     * Get the distance threshold for breeding (how close mates need to be)
     * @return distance in pixels
     */
    public abstract double getBreedingRange();

    protected List<Fence> getFencesInVision(Grid grid){
        int searchRadius = GameConfig.getSearchRadius(this.getClass());
        List<Object> nearby = grid.findNearby(worldX, worldY, searchRadius);
        List<Fence> fenceList = new ArrayList<>();

        for (Object obj : nearby) {
            if (obj instanceof Fence) {
                Fence fence = (Fence) obj;

                int dx = worldX - fence.getStartX();
                int dy = worldY - fence.getStartY();
                double dist = Math.sqrt(dx * dx + dy * dy); //pythagoras

                if (dist <= visionRadius) {
                    fenceList.add(fence);
                }
            }
        }//outer loop.

        return fenceList;
    }

    protected Vector2d getFenceForce(List <Fence> nearbyFences){
        double dist = Double.MAX_VALUE;
        Fence fence = null;

        //find closest fence.
        for (int i = 0; i < nearbyFences.size(); i ++){
            double dx = worldX - nearbyFences.get(i).getStartX();
            double dy = worldY - nearbyFences.get(i).getStartY();
            double currentDist = Math.sqrt(dx * dx + dy * dy); //pythagoras

            if (currentDist < dist ) {
                fence = nearbyFences.get(i);
                dist = currentDist;
            }
        }

        //find force:
        double dx = worldX - fence.getStartX();
        double dy = worldY - fence.getStartY();
        Vector2d force = new Vector2d(dx, dy);

        force.setMagnitude(speed); //set length.
        force.sub(currMovement);

        return force;
    }

}
