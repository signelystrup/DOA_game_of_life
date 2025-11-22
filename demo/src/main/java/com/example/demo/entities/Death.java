package com.example.demo.entities;

import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Death {
    static BufferedImage sprite;
    @Getter private int deathTimer = 60;
    private int worldX, worldY;

    public Death(int worldX, int worldY){
        this.worldX = worldX;
        this.worldY = worldY;
        loadSprite();
    }

    public void draw(Graphics2D g2) {
        if (deathTimer > 0) {
            g2.drawImage(sprite, worldX, worldY, 24, 24, null);
            deathTimer--;
        }
    }

    public void loadSprite(){
        if (sprite == null) {
            try {
                sprite = ImageIO.read(getClass().getResourceAsStream("/static/sprites/death.png"));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
