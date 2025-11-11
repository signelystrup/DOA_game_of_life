package com.example.demo.entities;

import java.awt.*;

public class Grass {
    //made it public because they were being called from other packages, we could also do setters/getters to deal with this in future.
    public int x, y;
    protected int gridX, gridY;

    public Grass(int x, int y){
        this.x = x;
        this.y = y;
        gridX = 0; //TODO: calculate.
        gridY = 0;
    }

    public void draw(Graphics2D g2){    }

    public void update(){

    }
}
