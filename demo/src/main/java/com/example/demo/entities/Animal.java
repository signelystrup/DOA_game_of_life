package com.example.demo.entities;

import java.awt.*;

import static java.lang.Math.sqrt;

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
    public void move(){
        try {
            int dx = (destX - worldX);
            int dy = (destY - worldY);
            int distance =(int) sqrt(dx*dx + dy*dy); //pythagoras.

            int step = distance / SPEED; //how many updates will it take to reach destination?

            worldX += dx / step; //take a single step
            worldY += dy / step;

        }catch(ArithmeticException e){ //can't divide by 0.
            worldX ++;
            //e.printStackTrace();
        }
    }
    public void update(){}

}
