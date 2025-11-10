package com.example.demo.entities;

public class Grass {
    protected int x, y;
    protected int gridX, gridY;

    public Grass(int x, int y){
        this.x = x;
        this.y = y;
        gridX = 0; //TODO: calculate.
        gridY = 0;
    }
}
