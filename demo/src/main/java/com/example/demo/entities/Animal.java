package com.example.demo.entities;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Animal {
    //made it public because they were being called from other packages, we could also do setters/getters to deal with this in future.
    public int x, y;
    protected int gridX, gridY;
    protected double dx, dy;
    protected int SPEED = 6;

    public Animal(int x, int y){
        this.x = x;
        this.y = y;
        gridX = 0; //TODO: calculate.
        gridY = 0;
    }

    public void draw(Graphics2D g2){}

    public void update(){}

}
