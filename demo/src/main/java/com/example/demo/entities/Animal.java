package com.example.demo.entities;

public class Animal {
    protected int x, y;
    protected int gridX, gridY;

    public Animal(int x, int y){
        this.x = x;
        this.y = y;
        gridX = 0; //TODO: calculate.
        gridY = 0;
    }
}
