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
public class Wolf extends Animal {
    static final double SPEED = 2.5;
    static final double VISION = 100.0;  // Wolves see a bit farther
    static BufferedImage sprite;

    public Wolf(int x, int y){
        super(x, y, SPEED, VISION);
        loadSprite();
    }

    @Override
    public void draw(Graphics2D g2){
        g2.drawImage(sprite, x, y, 24, 24, null);
    }

    @Override
    protected Vector2d calculateSteeringForce(Grid grid) {
        Vector2d steering = new Vector2d(0, 0);
        
        // Get nearby animals from grid
        List<Animal> nearbyAnimals = getAnimalsInVision(grid);
        List<Bunny> nearbyBunnies = filterByType(nearbyAnimals, Bunny.class);
        
        // SEEK bunnies (hunt them!)
        if (!nearbyBunnies.isEmpty()) {
            Vector2d seekForce = seekBunny(nearbyBunnies);
            seekForce.mult(2.0);  // Strong hunting instinct
            steering.add(seekForce);
        }
        
        steering.limit(maxForce);
        return steering;
    }
    
    // Seek: hunt the closest bunny
    private Vector2d seekBunny(List<Bunny> bunnies) {
        // Find closest bunny
        Bunny closest = null;
        double minDist = Double.MAX_VALUE;
        
        for (Bunny bunny : bunnies) {
            double dx = x - bunny.getX();
            double dy = y - bunny.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            
            if (dist < minDist) {
                minDist = dist;
                closest = bunny;
            }
        }
        
        if (closest == null) return new Vector2d(0, 0);
        
        Vector2d desired = new Vector2d(closest.getX() - x, closest.getY() - y);
        desired.normalize();
        desired.mult(speed);
        
        Vector2d steer = desired.copy();
        steer.sub(velocity);
        
        return steer;
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
