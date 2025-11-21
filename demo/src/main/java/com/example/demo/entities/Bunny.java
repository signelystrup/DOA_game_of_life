package com.example.demo.entities;

import com.example.demo.GameConfig;
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
    static final double VISION = 40.0;  // Vision radius in pixels
    static BufferedImage sprite;

    private boolean hasEaten = false;  // Boolean for eaten grass
    private Bunny breedingPartner = null;  // Track who we're breeding with

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

        // 1. FLEE from wolves (HIGHEST priority!)
        if (!nearbyWolves.isEmpty()) {
            Vector2d fleeForce = flee(nearbyWolves);
            fleeForce.mult(4.0);  // Very strong - survival first!
            steering.add(fleeForce);
            return steering;  // Ignore everything else when fleeing
        }

        // 2. BREEDING behavior (if has eaten)
        if (hasEaten) {
            // Look for another bunny that has also eaten
            Bunny mate = findMate(nearbyBunnies);

            if (mate != null) {
                // Move toward mate for breeding
                Vector2d breedingForce = seekMate(mate);
                breedingForce.mult(3.0);  // High priority
                steering.add(breedingForce);
                breedingPartner = mate;  // Remember our partner
            } else {
                // No mate nearby, do cohesion with other fed bunnies
                List<Bunny> fedBunnies = getFedBunnies(nearbyBunnies);
                if (!fedBunnies.isEmpty()) {
                    Vector2d cohesionForce = cohesion(fedBunnies);
                    cohesionForce.mult(1.0);
                    steering.add(cohesionForce);
                }
            }
        } else {
            // 3. SEEK grass (when hungry)
            if (!nearbyGrass.isEmpty()) {
                Vector2d seekForce = seekGrass(nearbyGrass);
                seekForce.mult(2.0);  // Medium-high priority
                steering.add(seekForce);
            }
        }

        steering.limit(maxForce);
        return steering;
    }

    // Find a mate (another bunny that has eaten)
    private Bunny findMate(List<Bunny> bunnies) {
        for (Bunny other : bunnies) {
            if (other != this && other.hasEaten() && other != breedingPartner) {
                return other;
            }
        }
        return null;
    }

    // Get list of fed bunnies for cohesion
    private List<Bunny> getFedBunnies(List<Bunny> bunnies) {
        List<Bunny> fed = new java.util.ArrayList<>();
        for (Bunny bunny : bunnies) {
            if (bunny.hasEaten()) {
                fed.add(bunny);
            }
        }
        return fed;
    }

    // Seek toward mate for breeding
    private Vector2d seekMate(Bunny mate) {
        Vector2d desired = new Vector2d(mate.getWorldX() - worldX,
                mate.getWorldY() - worldY);
        desired.normalize();
        desired.mult(speed);

        Vector2d steer = desired.copy();
        steer.sub(currMovement);

        return steer;
    }

    // Check if close enough to breed with another bunny
    public boolean canBreedWith(Bunny other) {
        if (!this.hasEaten || !other.hasEaten()) {
            return false;
        }

        double dx = worldX - other.getWorldX();
        double dy = worldY - other.getWorldY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        return dist < 20;  // Must be within 20 pixels
    }

    // Reset after breeding
    public void resetAfterBreeding() {
        hasEaten = false;
        breedingPartner = null;
    }

    // Flee: move AWAY from wolves
    private Vector2d flee(List<Wolf> wolves) {
        Vector2d fleeDir = new Vector2d(0, 0);

        for (Wolf wolf : wolves) {
            double dx = worldX - wolf.getWorldX();
            double dy = worldY - wolf.getWorldY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist > 0 && dist < visionRadius) {
                Vector2d away = new Vector2d(dx, dy);
                away.normalize();
                away.div(dist);  // Closer wolves = stronger flee
                fleeDir.add(away);
            }
        }

        if (fleeDir.magnitude() > 0) {
            fleeDir.normalize();
            fleeDir.mult(speed);
            fleeDir.sub(currMovement);
        }

        return fleeDir;
    }

    // Seek: move TOWARD grass
    private Vector2d seekGrass(List<Grass> grassList) {
        // Find closest grass
        Grass closest = null;
        double minDist = Double.MAX_VALUE;

        for (Grass grass : grassList) {
            double dx = worldX - grass.getWorldX();
            double dy = worldY - grass.getWorldY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < minDist) {
                minDist = dist;
                closest = grass;
            }
        }

        if (closest == null) return new Vector2d(0, 0);

        Vector2d desired = new Vector2d(closest.getWorldX() - worldX,
                closest.getWorldY() - worldY);
        desired.normalize();
        desired.mult(speed);

        Vector2d steer = desired.copy();
        steer.sub(currMovement);

        return steer;
    }

    // Cohesion: move toward center of bunny group
    private Vector2d cohesion(List<Bunny> bunnies) {
        if (bunnies.isEmpty()) return new Vector2d(0, 0);

        Vector2d center = new Vector2d(0, 0);

        for (Bunny bunny : bunnies) {
            center.add(new Vector2d(bunny.getWorldX(), bunny.getWorldY()));
        }
        center.div(bunnies.size());

        Vector2d desired = new Vector2d(center.x - worldX, center.y - worldY);
        desired.normalize();
        desired.mult(speed);

        Vector2d steer = desired.copy();
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
        breedingPartner = null;
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
