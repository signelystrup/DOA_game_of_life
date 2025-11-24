package com.example.demo.entities;

import com.example.demo.GameConfig;
import com.example.demo.Grid;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class Wolf extends Animal {
    static final double SPEED = 2.5;
    static final double VISION = 80.0;  // Wolves see farther than bunnies
    static BufferedImage sprite;
    private boolean hasEaten = false;
    private Wolf breedingPartner = null;

    public Wolf(int x, int y){
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

        // Get nearby animals from grid
        List<Animal> nearbyAnimals = getAnimalsInVision(grid);
        List<Bunny> nearbyBunnies = filterByType(nearbyAnimals, Bunny.class);
        List<Wolf> nearbyWolves = filterByType(nearbyAnimals, Wolf.class);
        List<Fence> nearbyFences = getFencesInVision(grid);

        // 1. BREEDING behavior (if has eaten)
        if (hasEaten) {
            // Look for another wolf that has also eaten
            Wolf mate = findMate(nearbyWolves);

            if (mate != null) {
                // Move toward mate for breeding
                Vector2d breedingForce = seekMate(mate);
                breedingForce.mult(3.0);  // High priority
                steering.add(breedingForce);
                breedingPartner = mate;
            } else {
                // No mate nearby, do cohesion with other fed wolves
                List<Wolf> fedWolves = getFedWolves(nearbyWolves);
                if (!fedWolves.isEmpty()) {
                    Vector2d cohesionForce = cohesion(fedWolves);
                    cohesionForce.mult(1.0);
                    steering.add(cohesionForce);
                }
            }
        } else {
            // 2. SEEK bunnies (hunt them when hungry!)
            if (!nearbyBunnies.isEmpty()) {
                Vector2d seekForce = seekBunny(nearbyBunnies);
                seekForce.mult(2.0);  // Strong hunting instinct
                steering.add(seekForce);
            }
        }

        // 3. avoid nearby fences:
        if (!nearbyFences.isEmpty()){
            Vector2d fenceForce = getFenceForce(nearbyFences);
            fenceForce.mult(3.0);
            steering.add(fenceForce);
        }

        steering.limit(maxForce);
        return steering;
    }

    // Seek: hunt the closest bunny
    private Vector2d seekBunny(List<Bunny> bunnies) {
        // Find closest bunny
        Bunny nearestBunny = null;
        double minDist = Double.MAX_VALUE;

        for (Bunny bunny : bunnies) {
            double dx = worldX - bunny.getWorldX();
            double dy = worldY - bunny.getWorldY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < minDist) {
                minDist = dist;
                nearestBunny = bunny;
            }
        }

        if (nearestBunny == null) return new Vector2d(0, 0);

        Vector2d idealPath = new Vector2d(nearestBunny.getWorldX() - worldX, nearestBunny.getWorldY() - worldY);
        idealPath.normalize();
        idealPath.mult(speed);

        Vector2d steer = idealPath.copy();
        steer.sub(currMovement);

        return steer;
    }

    // Find a mate (another wolf that has eaten)
    private Wolf findMate(List<Wolf> wolves) {
        for (Wolf other : wolves) {
            if (other != this && other.hasEaten() && other != breedingPartner) {
                return other;
            }
        }
        return null;
    }

    // Get list of fed wolves for cohesion
    private List<Wolf> getFedWolves(List<Wolf> wolves) {
        List<Wolf> fed = new java.util.ArrayList<>();
        for (Wolf wolf : wolves) {
            if (wolf.hasEaten()) {
                fed.add(wolf);
            }
        }
        return fed;
    }

    // Seek toward mate for breeding
    private Vector2d seekMate(Wolf mate) {
        Vector2d desired = new Vector2d(mate.getWorldX() - worldX,
                mate.getWorldY() - worldY);
        desired.normalize();
        desired.mult(speed);

        Vector2d steer = desired.copy();
        steer.sub(currMovement);

        return steer;
    }

    // Cohesion: move toward center of wolf pack
    private Vector2d cohesion(List<Wolf> wolves) {
        if (wolves.isEmpty()) return new Vector2d(0, 0);

        Vector2d center = new Vector2d(0, 0);

        for (Wolf wolf : wolves) {
            center.add(new Vector2d(wolf.getWorldX(), wolf.getWorldY()));
        }
        center.div(wolves.size());

        Vector2d desired = new Vector2d(center.x - worldX, center.y - worldY);
        desired.normalize();
        desired.mult(speed);

        Vector2d steer = desired.copy();
        steer.sub(currMovement);

        return steer;
    }

    // Check if close enough to breed with another wolf
    @Override
    public boolean canBreedWith(Animal other) {
        // Must be same species
        if (!(other instanceof Wolf)) {
            return false;
        }

        Wolf otherWolf = (Wolf) other;

        if (!this.hasEaten || !otherWolf.hasEaten()) {
            return false;
        }

        double dx = worldX - otherWolf.getWorldX();
        double dy = worldY - otherWolf.getWorldY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        return dist < 20;  // Must be within 20 pixels
    }

    // Reset after breeding
    public void resetAfterBreeding() {
        hasEaten = false;
        breedingPartner = null;
    }

    public void eatBunny() {
        hasEaten = true;
        resetStarvationTimer();  // Reset timer when eating
    }

    public boolean hasEaten() {
        return hasEaten;
    }

    public void resetEaten() {
        hasEaten = false;
        breedingPartner = null;
    }

    /**
     * Override to handle two-stage starvation:
     * - Fed wolves: 10 seconds to breed, then become hungry again
     * - Hungry wolves: 10 seconds to find food (bunny), or die
     */
    @Override
    public void incrementStarvationTimer() {
        framesSinceLastAte++;

        // Fed wolf reaches 10 seconds without breeding
        if (framesSinceLastAte >= STARVATION_THRESHOLD && hasEaten) {
            hasEaten = false;           // Become hungry again
            framesSinceLastAte = 0;     // Reset timer - now have 10 seconds to find food
        }
    }

    // Implement abstract methods from Animal class

    @Override
    public boolean canEat(Object entity) {
        return entity instanceof Bunny;
    }

    @Override
    public void eat(Object entity) {
        if (entity instanceof Bunny) {
            eatBunny();
        }
    }

    @Override
    public double getEatingRange() {
        return 15.0;  // Wolves need to be within 15 pixels to catch bunnies
    }

    @Override
    public double getBreedingRange() {
        return 20.0;  // Wolves need to be within 20 pixels to breed
    }

    public void loadSprite(){
        if (sprite == null) {
            try {
                sprite = ImageIO.read(getClass().getResourceAsStream("/static/sprites/wolf.png"));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
