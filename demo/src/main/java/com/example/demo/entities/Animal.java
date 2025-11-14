package com.example.demo.entities;

import com.example.demo.Grid;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Animal {
    protected int x, y;
    protected int gridX, gridY;
    
    // Add flocking stuff
    protected Vector2d velocity;
    protected double speed;
    protected double visionRadius;
    protected double maxForce = 0.3;

    public Animal(int x, int y, double speed, double visionRadius){
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.visionRadius = visionRadius;
        
//        // Initialize with random velocity
//        this.velocity = new Vector2d(Math.random() - 0.5, Math.random() - 0.5);
//        this.velocity.setMagnitude(speed);
//
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
        x += (int)velocity.x;
        y += (int)velocity.y;
        
        // Wrap around edges (500x500 from GamePanel)
        if (x < 0) x = 500;
        if (x > 500) x = 0;
        if (y < 0) y = 500;
        if (y > 500) y = 0;
    }
    
    protected abstract Vector2d calculateSteeringForce(Grid grid);
    
    // Helper: Get animals in vision from grid
    protected List<Animal> getAnimalsInVision(Grid grid) {
        List<Object> nearby = grid.findNearby(x, y);
        List<Animal> animals = new ArrayList<>();
        
        for (Object obj : nearby) {
            if (obj instanceof Animal) {
                Animal other = (Animal) obj;
                if (other != this) {
                    double dist = Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
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
        List<Object> nearby = grid.findNearby(x, y);
        List<Grass> grassList = new ArrayList<>();
        
        for (Object obj : nearby) {
            if (obj instanceof Grass) {
                Grass grass = (Grass) obj;
                double dist = Math.sqrt((x - grass.getX()) * (x - grass.getX()) + 
                                      (y - grass.getY()) * (y - grass.getY()));
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
