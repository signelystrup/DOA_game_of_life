package com.example.demo.entities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Grass {
    protected int x, y;
    protected int gridX, gridY;
    private BufferedImage sprite;

    public Grass(int x, int y){
        this.x = x;
        this.y = y;
        gridX = 0; //TODO: calculate.
        gridY = 0;

        loadSprite();
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

    public void draw(Graphics2D g2){
        g2.drawImage(sprite, x, y, 24, 24, null);
    }

    public void update(){

    }
}
