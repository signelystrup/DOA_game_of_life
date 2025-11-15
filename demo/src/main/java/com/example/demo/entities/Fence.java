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

            int dx = 24;
            int dy = 24;

            if (prevX - startX > 0){
                dx *= -1;
            }
            if (prevY - startY > 0){
                dy *= -1;
            }

            endY = startY + dy;
            endX = startX + dx;

            Random r = new Random();
            int direction = (r.nextInt(0,2) - 1);

            try{
                a = (prevX - startX)/(prevY - startY); //calculate slope of prev segment.

                calculateEndPoint(dx, direction);

            }catch (ArithmeticException e){//divide by 0
                endY = startY + dy * direction; //only y can change.
            }

            System.out.println("a: " + a);
            System.out.println("\ndirection: " + direction + "\ndx, dy: " + dx + ", " + dy);

            //System.out.println("\nx1: " + this.startX + ", y1: " + this.startY + "\nx2: " + endX + ", y2: " + endY);
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
                    if (direction == -1){ //counter-clockwise
                        endX = startX;
                    }else if (direction == 1){ //clockwise
                        endY = startY;
                    }
                    break;
                default:
                    break;
            }

            System.out.println("(" + endX + ", " + endY + ")");

            //endX = startX + dx * (a == 2 ? a + direction : a);
            //endY = startY + dx * (a + r.nextInt(-1,1));
            //endX = horizontal ? startX + dx : start * (a + r.nextInt(2) - 1);
            //endY = startY + dx * (a + r.nextInt(2) - 1);

            //boo.
            /*
            switch (a){
                case 1:
                    if (direction == 0 || direction == 1){
                        endX = startX + dx;
                    }

                    if (direction == -1 || direction == 0){
                        endY = startY + dx;
                    }

                    break;

                case 0:

                    if (direction == -1 ){
                        endX = st
                    }

                    endY = startY + dx;

                    break;
                case -1:
                    break;
                default:
                    break;
            }*/
        }

        private void calculateEndX(){
            //f(x) = ax + b
            //ax + b


            //1/8 directions. --> 1/3 directions.
            // +/- x
            // +/- y.

            // x1 > x2
            // y1 > y2.

        }

        private int calculateEndY(int y1, int y2, int distance){
            Random r = new Random();
            int multiplier = r.nextInt(0,2) - 1;
            if (y1 == y2){
                return y2 + distance * multiplier;
            }else if (y1 - y2 < 0){ //heading down
                return y2 + distance;
            }else{
                return y2 - distance;
            }
        }


    }



    private String orientation; //"horizontal", "vertical", "s_diagonal", "z_diagonal" (?)
    public Fence (int length){
        fence = new Segment[length];

        Random r = new Random();
        int startX = r.nextInt(0,500);
        int startY = r.nextInt(0,500);
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
