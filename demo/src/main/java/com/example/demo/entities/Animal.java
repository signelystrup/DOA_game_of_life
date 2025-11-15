package com.example.demo.entities;

import java.awt.*;

public class Animal {
    protected int worldX, worldY;
    protected double dx, dy;
    protected int SPEED = 6;

    protected int destX, destY;

    public Animal(int worldX, int worldY){
        this.worldX = worldX;
        this.worldY = worldY;
    }

    public void draw(Graphics2D g2){}
    public void move(){}
    public void update(){}

}
