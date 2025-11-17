package com.example.demo.entities;

import java.awt.*;

public class Grass {
    protected int worldX, worldY;
    protected int gridX, gridY;

    public Grass(int worldX, int worldY){
        this.worldX = worldX;
        this.worldY = worldY;
        gridX = 0; //TODO: calculate.
        gridY = 0;
    }

    public void draw(Graphics2D g2){
        // Draw grass as green square
        g2.setColor(Color.GREEN);
        g2.fillRect(worldX, worldY, 16, 16);
    }

    public void update(){

    }
    
    // Getters
    public int getWorldX() { return worldX; }
    public int getWorldY() { return worldY; }
}
