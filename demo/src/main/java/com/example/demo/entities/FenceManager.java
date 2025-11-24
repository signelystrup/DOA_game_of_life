package com.example.demo.entities;

import com.example.demo.GameConfig;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@Getter
public class FenceManager {
    private static BufferedImage verticalSprite, horizontalSprite, zDiagonalSprite, sDiagonalSprite;
     private Fence[] segments;

    public FenceManager(int length){
        segments = new Fence[length];

        //create segments:
        Random r = new Random();
        int startX = r.nextInt(0, GameConfig.WORLD_WIDTH);
        int startY = r.nextInt(0, GameConfig.WORLD_HEIGHT);
        int prevX = startX - 10;
        int prevY = startY  - 10;

        for (int i = 0; i < length; i ++){
            segments[i] = new Fence(startX, startY, prevX, prevY);

            startX = segments[i].getEndX();
            startY = segments[i].getEndY();
            prevX = segments[i].getStartX();
            prevY = segments[i].getStartY();
        }

        loadSprites();
    }

    public void draw(Graphics2D g2){
        BufferedImage sprite;

        for (int i = 0; i < segments.length; i ++) {
            Fence fence = segments[i];

            int height = 24;
            int startY = Math.min(fence.getStartY(), fence.getEndY()) - height;
            int startX = Math.min(fence.getStartX(), fence.getEndX());

            
            if (fence.getA() == 0){
                sprite = verticalSprite;
                startX -= 18;
                height *= 2;

            }else if (fence.getA() == -1){
                sprite = zDiagonalSprite;
                height *= 2;
            }else if (fence.getA() == 1){
                sprite = sDiagonalSprite;
                height *= 2;
            }else{
                sprite = horizontalSprite;
            }

            g2.drawImage(sprite, startX, startY, 24, height, null);
        }

    }

    public void loadSprites(){
        if (verticalSprite == null || horizontalSprite == null || sDiagonalSprite == null || zDiagonalSprite == null) {
            try {
                verticalSprite   = ImageIO.read(getClass().getResourceAsStream("/static/sprites/fence/fence_vertical.png"));
                horizontalSprite = ImageIO.read(getClass().getResourceAsStream("/static/sprites/fence/fence_horizontal.png"));
                sDiagonalSprite  = ImageIO.read(getClass().getResourceAsStream("/static/sprites/fence/fence_diagonal_down.png"));
                zDiagonalSprite  = ImageIO.read(getClass().getResourceAsStream("/static/sprites/fence/fence_diagonal_up.png"));

            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
