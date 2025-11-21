package com.example.demo.entities;

import com.example.demo.GameConfig;
import com.example.demo.Grid;
import lombok.Getter;
import lombok.Setter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    // Helper: Get animals in vision from grid
    protected List<Animal> getAnimalsInVision(Grid grid) {
        List<Object> nearby = grid.findNearby(worldX, worldY);
        List<Animal> animals = new ArrayList<>();

        for (Object obj : nearby) {
            if (obj instanceof Animal) {
                Animal other = (Animal) obj;
                if (other != this) {
                    double dist = Math.sqrt((worldX - other.worldX) * (worldX - other.worldX) + 
                                          (worldY - other.worldY) * (worldY - other.worldY));
                    if (dist <= visionRadius) {
                        animals.add(other);
                    }
                }
            }
        }

        return animals;
    }

    // Helper: Get grass in vision from grid
    protected List<Grass> getGrassInVision(Grid grid) {
        List<Object> nearby = grid.findNearby(worldX, worldY);
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

    protected List<Fence> getFencesInVision(Grid grid){
        List<Object> nearby = grid.findNearby(worldX, worldY);
        List<Fence> fenceList = new ArrayList<>();

        for (Object obj : nearby) {
            if (obj instanceof FenceManager) {
                FenceManager fenceManager = (FenceManager) obj;
                Fence[] segments = fenceManager.getSegments();

                for (int i = 0 ; i < segments.length; i++) {
                    int dx = worldX - segments[i].getStartX();
                    int dy = worldY - segments[i].getStartY();
                    double dist = Math.sqrt(dx * dx + dy * dy); //pythagoras

                    if (dist <= visionRadius) {
                        fenceList.add(segments[i]);
                    }
                }//inner loop
            }
        }//outer loop.

        return fenceList;
    }
}
