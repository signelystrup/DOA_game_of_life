package com.example.demo.entities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Grass {
    protected int worldX, worldY;
    protected int gridX, gridY;
    static BufferedImage sprite;

    public Grass(int worldX, int worldY){
        this.worldX = worldX;
        this.worldY = worldY;
        gridX = 0; //TODO: calculate.
        gridY = 0;
        loadSprite();
    }

    public void draw(Graphics2D g2){
        // Draw grass sprite if loaded, otherwise draw green square
        if (sprite != null) {
            g2.drawImage(sprite, worldX, worldY, 16, 16, null);
        } else {
            g2.setColor(Color.GREEN);
            g2.fillRect(worldX, worldY, 16, 16);
        }
    }

    public void update(){

    }
    
    public void loadSprite(){
        if (sprite == null) {
            try {
                // Assuming grass sprite exists in the sprites folder
                sprite = ImageIO.read(getClass().getResourceAsStream("/static/sprites/grass.png"));
            } catch(IOException e) {
                System.err.println("Could not load grass sprite: " + e.getMessage());
                // Will fall back to green square
            } catch(IllegalArgumentException e) {
                System.err.println("Grass sprite not found, using fallback");
            }
        }
    }
    
    // Getters
    public int getWorldX() { return worldX; }
    public int getWorldY() { return worldY; }
}
