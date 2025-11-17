package com.example.demo.entities;

import com.example.demo.GameConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class  Wolf extends Animal {
    static final int SPEED = 6;
    static BufferedImage sprite;

    public Wolf(int worldX, int worldY){
        super(worldX, worldY);
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
        destX = r.nextInt(0, GameConfig.WORLD_WIDTH);
        destY = r.nextInt(0, GameConfig.WORLD_HEIGHT);
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
