package com.example.demo.entities;

import com.example.demo.GameConfig;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class FenceManager {
    private static BufferedImage verticalSprite, horizontalSprite, zDiagonalSprite, sDiagonalSprite;
    @Getter private Fence[] segments;

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
        g2.setColor(Color.BLACK);

        /*
        for (int i = 0; i < segments.length; i ++) {
            Fence segment = segments[i];
            BufferedImage sprite;


            int width = 24;
            int height = 24;
            int x = Math.min(segment.getStartX(), segment.getEndX()) ;
            int y = Math.max(segment.getStartY(), segment.getEndX()) - height ;

            if (segment.getStartX() == segment.getEndX()){
                sprite = verticalSprite;
                x -= 20;
            }else if (segment.getStartY() == segment.getEndY()){
                sprite = horizontalSprite;
            }else if (     segment.getStartX() < segment.getEndX()
                        && segment.getStartY() < segment.getEndY()
                        || segment.getStartX() > segment.getEndX()
                        && segment.getStartY() > segment.getEndY()){
                sprite = sDiagonalSprite;
                height *= 2;
                y -= 16;
            }else{
                sprite = zDiagonalSprite;
                height *= 2;
                y -= 16;
            }

            //g2.drawLine(fence[i].getStartX(), fence[i].getStartY(), fence[i].getEndX(), fence[i].getEndY());

            g2.drawImage(sprite, x, y, width, height, null);
        }//end of for loop.


         */

        BufferedImage sprite;


        g2.setColor(Color.WHITE);

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
