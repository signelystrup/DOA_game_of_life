package com.example.demo.entities;

import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Heart {
    static BufferedImage sprite;
    @Getter private int heartTimer = 60;
    private int worldX, worldY;

    public Heart(int worldX, int worldY){
        this.worldX = worldX;
        this.worldY = worldY;
        loadSprite();

        System.out.println("heart");
    }

    public void draw(Graphics2D g2) {
        if (heartTimer > 0) {
            g2.drawImage(sprite, worldX, worldY, 24, 24, null);
            heartTimer--;
            System.out.println("draw heart");
        }
    }

    public void loadSprite(){
        if (sprite == null) {
            try {
                sprite = ImageIO.read(getClass().getResourceAsStream("/static/sprites/heart.png"));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
