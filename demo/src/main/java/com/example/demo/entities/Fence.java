package com.example.demo.entities;

import com.example.demo.GameConfig;
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

        public Segment(int startX, int startY, int prevX, int prevY) {
            this.startX = startX;
            this.startY = startY;

            int dx = 24;
            int dy = 24;

            if (prevX - startX > 0) {
                dx *= -1;
            }
            if (prevY - startY > 0) {
                dy *= -1;
            }

            //set default values for end point.
            endY = startY + dy;
            endX = startX + dx;

            //get random direction (turn clockwise, continue straight ahead, turn counter-clockwise
            Random r = new Random();
            int direction = (r.nextInt(0, 2) - 1);

            try {
                a = (prevX - startX) / (prevY - startY); //calculate slope (a) of prev segment.

                calculateEndPoint(dx, direction);

            } catch (ArithmeticException e) {//divide by 0. If line is horizontal.
                endY = startY + dy * direction; //only y can change.
            }
        }

        public void calculateEndPoint(int dx, int direction){

            switch(a){
                case -1:
                    if (direction == -1){ //counter-clockwise --> horizontal.
                        endY = startY;
                    }else if (direction == 1){ //clockwise --> vertical.
                        endX = startX;
                    }
                    break;
                case 0: //vertical
                    endX = startX + dx * direction; //only x can change.
                    break;
                case 1:
                    if (direction == -1){ //counter-clockwise --> vertical
                        endX = startX;
                    }else if (direction == 1){ //clockwise --> horizontal.
                        endY = startY;
                    }
                    break;
                default:
                    break;
            }//eo switch.
        }

    }

    public Fence (int length){
        fence = new Segment[length];

        Random r = new Random();
        int startX = r.nextInt(0, GameConfig.WORLD_WIDTH);
        int startY = r.nextInt(0, GameConfig.WORLD_HEIGHT);
        int prevX = startX - 10;
        int prevY = startY  - 10;

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
            if (i % 2 == 0) {
                g2.setColor(Color.RED);
            } else {
                g2.setColor(Color.BLACK);
            }
        }//g2.drawImage(sprite, x, y, 24, 24, null);
    }
}
