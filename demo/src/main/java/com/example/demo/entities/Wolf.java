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
        List <Fence> nearbyFences = getFencesInVision(grid);

        // SEEK bunnies (hunt them!)
        if (!nearbyBunnies.isEmpty()) {
            Vector2d seekForce = seekBunny(nearbyBunnies);
            seekForce.mult(2.0);  // Strong hunting instinct
            steering.add(seekForce);
        }

        // 4. avoid nearby fences:
        if (!nearbyFences.isEmpty()){
            Vector2d fenceForce = getFenceForce(nearbyFences);
            fenceForce.mult(3.0);  //idk.
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

    public boolean hasEaten() {
        return hasEaten;
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
