package com.example.demo.entities;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class Grass {
    protected int x, y;
    protected int gridX, gridY;

    public Grass(int x, int y){
        this.x = x;
        this.y = y;
        gridX = 0; //TODO: calculate.
        gridY = 0;
    }

    public void draw(Graphics2D g2){
    }

    public void update(){

    }
    

}
