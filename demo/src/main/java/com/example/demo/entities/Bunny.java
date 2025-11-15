package com.example.demo.entities;

import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import static java.lang.Math.sqrt;

@Getter
public class Bunny extends Animal {
    static final int SPEED = 3;
    static BufferedImage sprite;

    public Bunny(int worldX, int worldY){
        super(worldX, worldY);
        findDest();

        loadSprite();
    }

    @Override
    public void draw(Graphics2D g2){
        g2.drawImage(sprite, worldX, worldY, 24, 24, null);
    }

    @Override
    public void update(){
        if (worldX == destX && worldY == destY){
            findDest();
        }

        move();
    }

    @Override
    public void move(){
       super.move();
    }

    public void findDest(){
        //rewrite with find path:
        Random r = new Random();
        destX = r.nextInt(0,500);
        destY = r.nextInt(0,500);
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
