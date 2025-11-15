package com.example.demo.entities;

import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Fence {
    private static BufferedImage verticalSprite, horizontalSprite, zDiagonalSprite, sDiagonalSprite;
    private Segment[] fence;

    @Getter
    private class Segment{
        private int startX, startY, endX, endY;
        private int a = 2;

        public Segment(int startX, int startY, int prevX, int prevY){
            this.startX = startX;
            this.startY = startY;

            boolean horizontal = false;

            try{
                a = (prevX -startX)/(prevY - startY); //calculate slope of prev segment.
            }catch (ArithmeticException e){
                //divide by 0 (horizontally oriented)(?)
                horizontal = true;
            }

            Random r = new Random();
            int direction = r.nextInt();

           // int newDirection = a + direction;
            int distance = 24;

            

            //endX = startX + distance * (a == 2 ? a + direction : a);
            //endY = startY + distance * (a + r.nextInt(-1,1));
            //endX = horizontal ? startX + distance : start * (a + r.nextInt(2) - 1);
            //endY = startY + distance * (a + r.nextInt(2) - 1);

            //boo.
            /*
            switch (a){
                case 1:
                    if (direction == 0 || direction == 1){
                        endX = startX + distance;
                    }

                    if (direction == -1 || direction == 0){
                        endY = startY + distance;
                    }

                    break;

                case 0:

                    if (direction == -1 ){
                        endX = st
                    }

                    endY = startY + distance;

                    break;
                case -1:
                    break;
                default:
                    break;
            }*/

            System.out.println("a: " + a);

            //System.out.println("\nx1: " + this.startX + ", y1: " + this.startY + "\nx2: " + endX + ", y2: " + endY);
        }


    }



    private String orientation; //"horizontal", "vertical", "s_diagonal", "z_diagonal" (?)
    public Fence (int length){
        fence = new Segment[length];

        int startX = 100;
        int startY = 124;
        int prevX = 124;
        int prevY = 100;

        for (int i = 0; i < length; i ++){
            fence[i] = new Segment(startX, startY, prevX, prevY);

            startX = fence[i].getEndX();
            startY = fence[i].getEndY();
            prevX = fence[i].getStartX();
            prevY = fence[i].getStartY();
        }
    }

    public void draw(Graphics2D g2){
        g2.setColor(Color.BLACK);

        for (int i = 0; i < fence.length; i ++) {
            g2.drawLine(fence[i].getStartX(), fence[i].getStartY(), fence[i].getEndX(), fence[i].getEndY());
        }
        //g2.drawImage(sprite, x, y, 24, 24, null);
    }
}
