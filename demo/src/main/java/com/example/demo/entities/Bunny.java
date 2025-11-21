package com.example.demo.entities;

import com.example.demo.Grid;

import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@Getter
@Setter
public class Bunny extends Animal {
    static final double SPEED = 2.0;
    static final double VISION = 80.0;  // Vision radius in pixels
    static BufferedImage sprite;

    private boolean hasEaten = false;  // Boolean for eaten grass

    public Bunny(int x, int y){
        super(x, y, SPEED, VISION);
        loadSprite();
    }

    @Override
    public void draw(Graphics2D g2){
        g2.drawImage(sprite, worldX, worldY, 24, 24, null);
    }

    @Override
    protected Vector2d calculateSteeringForce(Grid grid) {
        Vector2d steering = new Vector2d(0, 0);

        // Get neighbors from grid
        List<Animal> nearbyAnimals = getAnimalsInVision(grid);
        List<Wolf> nearbyWolves = filterByType(nearbyAnimals, Wolf.class);
        List<Bunny> nearbyBunnies = filterByType(nearbyAnimals, Bunny.class);
        List<Grass> nearbyGrass = getGrassInVision(grid);
        List <Fence> nearbyFences = getFencesInVision(grid);

        // 1. FLEE from wolves (highest priority!)
        if (!nearbyWolves.isEmpty()) {
            Vector2d fleeForce = flee(nearbyWolves);
            fleeForce.mult(3.0);  // Strong weight
            steering.add(fleeForce);
        }

        // 2. SEEK grass
        if (!nearbyGrass.isEmpty()) {
            Vector2d seekForce = seekGrass(nearbyGrass);
            seekForce.mult(1.5);  // Medium-high weight
            steering.add(seekForce);
        }

        // 3. COHESION with other bunnies (only if has eaten)
        if (hasEaten && !nearbyBunnies.isEmpty()) {
            Vector2d cohesionForce = cohesion(nearbyBunnies);
            cohesionForce.mult(1.0);  // Medium weight
            steering.add(cohesionForce);
        }

        // 4. avoid nearby fences:
        if (!nearbyFences.isEmpty()){
            System.out.println("fences nearby"); //debug. remove.
            Vector2d fenceForce = getFenceForce(nearbyFences);
            fenceForce.mult(5.0);  // change
            steering.add(fenceForce);
        }

        steering.limit(maxForce);
        return steering;
    }

    // Flee: move AWAY from wolves
    private Vector2d flee(List<Wolf> wolves) {
        Vector2d fleeDir = new Vector2d(0, 0);

        for (Wolf wolf : wolves) {
            double dx = worldX - wolf.getWorldX();
            double dy = worldY - wolf.getWorldY();
            double dist = Math.sqrt(dx * dx + dy * dy); //pythagoras

            if (dist > 0 && dist < visionRadius) {
                Vector2d away = new Vector2d(dx, dy);
                away.normalize();
                away.div(dist);  // Closer wolves = stronger flee
                fleeDir.add(away);
            }
        }

        if (fleeDir.length() > 0) {
            fleeDir.normalize();
            fleeDir.mult(speed);
            fleeDir.sub(currMovement); //subtract
        }

        return fleeDir;
    }

    // Seek: move TOWARD grass
    private Vector2d seekGrass(List<Grass> grassList) {
        // Find closest grass
        Grass nearestGrass = null;
        double minDist = Double.MAX_VALUE; //highest possible double value. for finding smaller values.

        for (Grass grass : grassList) {
            double dx = worldX - grass.getWorldX();
            double dy = worldY - grass.getWorldY();
            double dist = Math.sqrt(dx * dx + dy * dy); //pythagoras

            if (dist < minDist) {
                minDist = dist;
                nearestGrass = grass;
            }
        }//end of for loop

        if (nearestGrass == null) return new Vector2d(0, 0); //if no grass found.

        Vector2d idealPath = new Vector2d(nearestGrass.getWorldX() - worldX, nearestGrass.getWorldY() - worldY);
        idealPath.normalize(); //find "step"
        idealPath.mult(speed);

        Vector2d steer = idealPath.copy();
        steer.sub(currMovement);

        return steer;
    }

    // Cohesion: move toward center of bunny group
    private Vector2d cohesion(List<Bunny> bunnies) {
        Vector2d center = new Vector2d(0, 0);

        for (Bunny bunny : bunnies) {
            center.add(new Vector2d(bunny.getWorldX(), bunny.getWorldY()));
        }
        center.div(bunnies.size());

        Vector2d idealPath = new Vector2d(center.x - worldX, center.y - worldY);
        idealPath.normalize();
        idealPath.mult(speed);

        Vector2d steer = idealPath.copy();
        steer.sub(currMovement);

        return steer;
    }

    public void eatGrass() {
        hasEaten = true;
    }

    public boolean hasEaten() {
        return hasEaten;
    }

    public void resetEaten() {
        hasEaten = false;
    }

    public void loadSprite(){
        if (sprite == null) {
            try {
                sprite = ImageIO.read(getClass().getResourceAsStream("/static/sprites/bunny.png"));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
