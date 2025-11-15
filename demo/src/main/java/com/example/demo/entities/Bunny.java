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

    public Bunny(int worldX, int worldY){
        super(worldX, worldY);

        destX = 300;
        destY = 300;

        loadSprite();
    }

    @Override
    public void draw(Graphics2D g2){
        g2.drawImage(sprite, worldX, worldY, 24, 24, null);
    }

    @Override
    public void update(){
        move();
    }

    @Override
    public void move(){
        try {
            int a = (destX - worldX) / (destY - worldY);
            worldX += a * SPEED;
            worldY += a * SPEED;
        }catch(ArithmeticException e){
            findPath();
        }
    }

    public void findPath(){
        Random r = new Random();
        destX = r.nextInt(0,500);
        destX = r.nextInt(0,500);
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
