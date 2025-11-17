package com.example.demo.entities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Grass {
    //made it public because they were being called from other packages, we could also do setters/getters to deal with this in future.
    public int worldX, worldY;
    protected int gridX, gridY;
    static BufferedImage sprite;

    public Grass(int worldX, int worldY){
        this.worldX = worldX;
        this.worldY = worldY;
        loadSprite();
    }

    public void draw(Graphics2D g2){
        g2.drawImage(sprite, worldX, worldY, 24, 24, null);
    }

    public void update(){

    }

    public void loadSprite(){
        if (sprite == null) {
            try {
                sprite = ImageIO.read(getClass().getResourceAsStream("/static/sprites/grass.png"));
            }catch(IOException e){
                e.printStackTrace();
            }//end of catch
        }//end of if.
    }
}
