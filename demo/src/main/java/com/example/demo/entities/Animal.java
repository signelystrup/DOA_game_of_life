package com.example.demo.entities;

import java.util.Random;

public class Animal {
    protected int x, y;
    protected int gridX, gridY;
    protected double dx, dy;
    protected int SPEED = 6;

    public Animal(int x, int y){
        this.x = x;
        this.y = y;
        gridX = 0; //TODO: calculate.
        gridY = 0;

        Random random = new Random();
        this.dx = calculateDx(random.nextDouble());
        this.dy = calculateDy(this.dx);
    }

    public double calculateDx(double dy){
        return SPEED/dy;
    }

    public double calculateDy(double dx){
        return SPEED/dx;
    }

}
