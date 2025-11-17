package com.example.demo.entities;

import com.example.demo.Grid;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

@Getter
@Setter
public abstract class Animal {
    protected int worldX, worldY;
    protected int gridX, gridY;

    // Add flocking stuff
    protected Vector2d velocity;
    protected double speed;
    protected double visionRadius;
    protected double maxForce = 0.3;

    protected int destX, destY;

    public Animal(int worldX, int worldY, double speed, double visionRadius){
        this.worldX = worldX;
        this.worldY = worldY;
        this.speed = speed;
        this.visionRadius = visionRadius;

        // Initialize with random velocity
        this.velocity = new Vector2d(Math.random() - 0.5, Math.random() - 0.5);
        this.velocity.setMagnitude(speed);

        gridX = 0; //TODO: calculate.
        gridY = 0;
    }

    public abstract void draw(Graphics2D g2);

    public void update(Grid grid){
        // Calculate flocking forces
        Vector2d steering = calculateSteeringForce(grid);

        // Apply steering
        velocity.add(steering);
        velocity.setMagnitude(speed);  // Keep speed constant

        // Update position
        worldX += (int)velocity.x;
        worldY += (int)velocity.y;

        // Wrap around edges (500x500 from GamePanel)
        if (worldX < 0) worldX = 500;
        if (worldX > 500) worldX = 0;
        if (worldY < 0) worldY = 500;
        if (worldY > 500) worldY = 0;
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
}
