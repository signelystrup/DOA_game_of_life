package com.example.demo.entities;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Fence {
    private static BufferedImage verticalSprite, horizontalSprite, zDiagonalSprite, sDiagonalSprite;
    private BufferedImage sprite;
    private int startX, startY, endX, endY;
    private String orientation; //"horizontal", "vertical", "s_diagonal", "z_diagonal" (?)
    public Fence (int startX, int startY, int endX, int endY){
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        this.orientation = orientation;

    }

    public void draw(Graphics2D g2){
        g2.setColor(Color.BLACK);
        g2.drawLine(startX, startY, endX, endY);
        //g2.drawImage(sprite, x, y, 24, 24, null);
    }
}
