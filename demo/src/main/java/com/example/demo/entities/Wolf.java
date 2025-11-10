package com.example.demo.entities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class  Wolf extends Animal {
    static final int SPEED = 6;
    static BufferedImage sprite;

    public Wolf(int x, int y){
        super(x, y);
        loadSprite();
    }

    @Override
    public void draw(Graphics2D g2){
        g2.drawImage(sprite, x, y, 24, 24, null);
    }

    @Override
    public void update(){

    }

    public void loadSprite(){
        if (sprite == null) {
            try {
                sprite = ImageIO.read(getClass().getResourceAsStream("/static/sprites/wolf.png"));
            }catch(IOException e){
                e.printStackTrace();
            }//end of catch
        }//end of if.
    }
}
