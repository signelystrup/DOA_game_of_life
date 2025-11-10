package com.example.demo.entities;

import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@Getter
public class Bunny extends Animal {
    static final int SPEED = 5;
    static BufferedImage sprite;

    public Bunny(int x, int y){
        super(x, y);
        loadSprite();
    }

    @Override
    public void draw(Graphics2D g2){

    }

    @Override
    public void update(){

    }

    public void loadSprite(){
        if (sprite == null) {
            try {
                sprite = ImageIO.read(getClass().getResourceAsStream("/static/sprites/bunny.png"));
            }catch(IOException e){
                e.printStackTrace();
            }//end of catch
        }//end of if.
    }

}
